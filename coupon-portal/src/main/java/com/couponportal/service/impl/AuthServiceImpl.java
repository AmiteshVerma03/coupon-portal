package com.couponportal.service.impl;

import com.couponportal.dto.request.LoginRequest;
import com.couponportal.dto.request.RefreshTokenRequest;
import com.couponportal.dto.request.RegisterRequest;
import com.couponportal.dto.response.AuthResponse;
import com.couponportal.entity.RefreshToken;
import com.couponportal.entity.Tenant;
import com.couponportal.entity.User;
import com.couponportal.exception.ResourceNotFoundException;
import com.couponportal.exception.UnauthorizedException;
import com.couponportal.repository.RefreshTokenRepository;
import com.couponportal.repository.TenantRepository;
import com.couponportal.repository.UserRepository;
import com.couponportal.security.JwtTokenProvider;
import com.couponportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final TenantRepository      tenantRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtTokenProvider      jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    // ── Register ────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tenant not found with id: " + request.getTenantId()));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .tenant(tenant)
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        String accessToken  = jwtTokenProvider.generateToken(user, tenant.getId());
        String refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    // ── Login ───────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken  = jwtTokenProvider.generateToken(user, user.getTenant().getId());
        String refreshToken = createRefreshToken(user);

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user, accessToken, refreshToken);
    }

    // ── Refresh Token ───────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new UnauthorizedException("Refresh token expired. Please login again.");
        }

        User   user        = storedToken.getUser();
        String accessToken = jwtTokenProvider.generateToken(user, user.getTenant().getId());

        return buildAuthResponse(user, accessToken, storedToken.getToken());
    }

    // ── Logout ──────────────────────────────────────────────

    @Override
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("User logged out, userId: {}", userId);
    }

    // ── Helpers ─────────────────────────────────────────────

    private String createRefreshToken(User user) {
        // delete old token if exists
        if (refreshTokenRepository.existsByUserId(user.getId())) {
            refreshTokenRepository.deleteByUserId(user.getId());
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiry / 1000))
                .build();

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .tenantId(user.getTenant().getId())
                .build();
    }
}
