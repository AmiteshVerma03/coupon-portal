package com.couponportal;

import com.couponportal.entity.Coupon;
import com.couponportal.entity.CouponRequest;
import com.couponportal.entity.RefreshToken;
import com.couponportal.entity.Tenant;
import com.couponportal.entity.User;
import com.couponportal.enums.RequestStatus;
import com.couponportal.enums.Role;
import com.couponportal.repository.CouponRepository;
import com.couponportal.repository.CouponRequestRepository;
import com.couponportal.repository.RefreshTokenRepository;
import com.couponportal.repository.TenantRepository;
import com.couponportal.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityHardeningIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponRequestRepository couponRequestRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void registerShouldRejectPrivilegedRoles() throws Exception {
        Tenant tenant = createTenant("reg-tenant");
        String email = uniqueEmail("forbidden-admin");

        Map<String, Object> payload = Map.of(
                "name", "Escalation Attempt",
                "email", email,
                "password", "Pass123!",
                "role", "ADMIN",
                "tenantId", tenant.getId()
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        assertThat(userRepository.findByEmail(email)).isEmpty();
    }

    @Test
    void registerShouldAllowUserRole() throws Exception {
        Tenant tenant = createTenant("reg-tenant-ok");
        String email = uniqueEmail("valid-user");

        Map<String, Object> payload = Map.of(
                "name", "Valid User",
                "email", email,
                "password", "Pass123!",
                "role", "USER",
                "tenantId", tenant.getId()
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void logoutShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    void roleBasedEndpointAccessShouldMatchPolicy() throws Exception {
        Tenant tenant = createTenant("role-tenant");
        User admin = createUser(tenant, Role.ADMIN, "admin");
        User manager = createUser(tenant, Role.MANAGER, "manager");
        User regularUser = createUser(tenant, Role.USER, "user");

        mockMvc.perform(get("/api/admin/users").with(authAs(admin)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/users").with(authAs(manager)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/manager/requests").with(authAs(manager)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/manager/requests").with(authAs(regularUser)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/user/profile").with(authAs(regularUser)))
                .andExpect(status().isOk());
    }

    @Test
    void adminCouponEndpointsMustBeTenantScoped() throws Exception {
        Tenant tenantOne = createTenant("tenant-one");
        Tenant tenantTwo = createTenant("tenant-two");
        User adminTenantOne = createUser(tenantOne, Role.ADMIN, "admin-one");

        Map<String, Object> createBody = Map.of(
                "code", "C-" + UUID.randomUUID(),
                "platform", "Udemy",
                "course", "Spring Security",
                "expiryDate", LocalDate.now().plusDays(30).toString(),
                "usageLimit", 5
        );

        mockMvc.perform(post("/api/admin/coupon")
                        .with(authAs(adminTenantOne))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tenantId").value(tenantOne.getId()));

        Coupon tenantTwoCoupon = couponRepository.save(Coupon.builder()
                .code("CROSS-" + UUID.randomUUID())
                .platform("Coursera")
                .course("Tenant Two Course")
                .expiryDate(LocalDate.now().plusDays(20))
                .usageLimit(2)
                .usedCount(0)
                .tenant(tenantTwo)
                .build());

        mockMvc.perform(get("/api/admin/coupon/{id}", tenantTwoCoupon.getId())
                        .with(authAs(adminTenantOne)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/admin/coupon/{id}", tenantTwoCoupon.getId())
                        .with(authAs(adminTenantOne)))
                .andExpect(status().isNotFound());

        assertThat(couponRepository.findById(tenantTwoCoupon.getId())).isPresent();
    }

    @Test
    void deleteUserShouldBeTenantScopedAndRemoveRefreshToken() throws Exception {
        Tenant tenantOne = createTenant("delete-tenant-one");
        Tenant tenantTwo = createTenant("delete-tenant-two");
        User adminTenantOne = createUser(tenantOne, Role.ADMIN, "deleter-admin");
        User sameTenantVictim = createUser(tenantOne, Role.USER, "same-tenant-user");
        User otherTenantVictim = createUser(tenantTwo, Role.USER, "other-tenant-user");

        refreshTokenRepository.save(RefreshToken.builder()
                .user(sameTenantVictim)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(delete("/api/admin/user/{id}", sameTenantVictim.getId())
                        .with(authAs(adminTenantOne)))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(sameTenantVictim.getId())).isEmpty();
        assertThat(refreshTokenRepository.existsByUserId(sameTenantVictim.getId())).isFalse();

        mockMvc.perform(delete("/api/admin/user/{id}", otherTenantVictim.getId())
                        .with(authAs(adminTenantOne)))
                .andExpect(status().isForbidden());

        assertThat(userRepository.findById(otherTenantVictim.getId())).isPresent();
    }

    @Test
    void userShouldWithdrawOnlyPendingOwnRequest() throws Exception {
        Tenant tenant = createTenant("withdraw-tenant");
        User user = createUser(tenant, Role.USER, "withdraw-user");

        CouponRequest pendingRequest = couponRequestRepository.save(CouponRequest.builder()
                .user(user)
                .course("Spring Boot")
                .platform("Udemy")
                .status(RequestStatus.PENDING)
                .build());

        CouponRequest approvedRequest = couponRequestRepository.save(CouponRequest.builder()
                .user(user)
                .course("Java")
                .platform("Coursera")
                .status(RequestStatus.APPROVED)
                .build());

        mockMvc.perform(delete("/api/user/request-coupon/{id}", pendingRequest.getId())
                        .with(authAs(user)))
                .andExpect(status().isOk());

        assertThat(couponRequestRepository.findById(pendingRequest.getId())).isEmpty();

        mockMvc.perform(delete("/api/user/request-coupon/{id}", approvedRequest.getId())
                        .with(authAs(user)))
                .andExpect(status().isBadRequest());
    }

    private Tenant createTenant(String prefix) {
        return tenantRepository.save(Tenant.builder()
                .name(prefix + "-" + UUID.randomUUID())
                .build());
    }

    private User createUser(Tenant tenant, Role role, String prefix) {
        return userRepository.save(User.builder()
                .name(prefix + "-name")
                .email(uniqueEmail(prefix))
                .password(passwordEncoder.encode("Pass123!"))
                .role(role)
                .tenant(tenant)
                .build());
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@example.com";
    }

    private RequestPostProcessor authAs(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        return SecurityMockMvcRequestPostProcessors.authentication(authentication);
    }
}
