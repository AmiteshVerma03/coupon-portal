package com.couponportal.repository;

import com.couponportal.entity.Coupon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // all coupons for a tenant — Task 1: now accepts Pageable
    // Task 1 — paginated version
    org.springframework.data.domain.Page<Coupon> findAllByTenantId(Long tenantId, Pageable pageable);

    // non-paginated version kept for internal service use (e.g. approval lookup)
    List<Coupon> findAllByTenantId(Long tenantId);

    // find available coupon for a specific course and platform within a tenant
    @Query("""
            SELECT c FROM Coupon c
            WHERE c.tenant.id = :tenantId
            AND c.course = :course
            AND c.platform = :platform
            AND c.usedCount < c.usageLimit
            AND c.expiryDate >= :today
            ORDER BY c.expiryDate ASC
            """)
    Optional<Coupon> findAvailableCoupon(
            @Param("tenantId") Long tenantId,
            @Param("course")   String course,
            @Param("platform") String platform,
            @Param("today")    LocalDate today
    );

    // Task 2 — DB-limited recommendations: Pageable.ofSize(3) replaces Java .limit(3)
    // Previously: findAllAvailableByTenantId loaded ALL available coupons, then Java streamed .limit(3)
    // Now:        the LIMIT is pushed into the SQL query — DB returns exactly N rows
    @Query("""
            SELECT c FROM Coupon c
            WHERE c.tenant.id = :tenantId
            AND c.usedCount < c.usageLimit
            AND c.expiryDate >= :today
            ORDER BY c.expiryDate ASC
            """)
    List<Coupon> findTopAvailableByTenantId(
            @Param("tenantId") Long tenantId,
            @Param("today")    LocalDate today,
            Pageable pageable
    );

    // full list (no limit) — still used by getAvailableCouponsForTenant (user view)
    @Query("""
            SELECT c FROM Coupon c
            WHERE c.tenant.id = :tenantId
            AND c.usedCount < c.usageLimit
            AND c.expiryDate >= :today
            """)
    List<Coupon> findAllAvailableByTenantId(
            @Param("tenantId") Long tenantId,
            @Param("today")    LocalDate today
    );

    boolean existsByCode(String code);
}
