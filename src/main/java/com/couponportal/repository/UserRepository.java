package com.couponportal.repository;

import com.couponportal.entity.User;
import com.couponportal.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Task 1 — paginated: all users in a tenant
    Page<User> findAllByTenantId(Long tenantId, Pageable pageable);

    // Non-paginated kept for internal use
    List<User> findAllByTenantId(Long tenantId);

    // users by role within a tenant
    List<User> findAllByTenantIdAndRole(Long tenantId, Role role);
}
