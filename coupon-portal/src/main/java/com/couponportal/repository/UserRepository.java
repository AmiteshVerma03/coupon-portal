package com.couponportal.repository;

import com.couponportal.entity.User;
import com.couponportal.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // all users belonging to a specific tenant
    List<User> findAllByTenantId(Long tenantId);

    // users by role within a tenant
    List<User> findAllByTenantIdAndRole(Long tenantId, Role role);
}