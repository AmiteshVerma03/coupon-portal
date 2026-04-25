package com.couponportal.repository;

import com.couponportal.entity.CouponRequest;
import com.couponportal.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRequestRepository extends JpaRepository<CouponRequest, Long> {

    // ── Task 1: paginated versions ──────────────────────────

    // User: their own requests — paginated
    Page<CouponRequest> findAllByUserId(Long userId, Pageable pageable);

    // Manager/Admin: all requests in tenant — paginated
    @Query("""
            SELECT cr FROM CouponRequest cr
            WHERE cr.user.tenant.id = :tenantId
            ORDER BY cr.createdAt DESC
            """)
    Page<CouponRequest> findAllByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    // Manager/Admin: filtered by status in tenant — paginated
    @Query("""
            SELECT cr FROM CouponRequest cr
            WHERE cr.user.tenant.id = :tenantId
            AND cr.status = :status
            ORDER BY cr.createdAt DESC
            """)
    Page<CouponRequest> findAllByTenantIdAndStatus(
            @Param("tenantId") Long tenantId,
            @Param("status")   RequestStatus status,
            Pageable pageable
    );

    // ── Non-paginated (kept for internal use) ───────────────

    List<CouponRequest> findAllByUserId(Long userId);

    @Query("""
            SELECT cr FROM CouponRequest cr
            WHERE cr.user.tenant.id = :tenantId
            ORDER BY cr.createdAt DESC
            """)
    List<CouponRequest> findAllByTenantIdList(@Param("tenantId") Long tenantId);

    @Query("""
            SELECT cr FROM CouponRequest cr
            WHERE cr.user.tenant.id = :tenantId
            AND cr.status = :status
            ORDER BY cr.createdAt DESC
            """)
    List<CouponRequest> findAllByTenantIdAndStatusList(
            @Param("tenantId") Long tenantId,
            @Param("status")   RequestStatus status
    );

    // Task 2 — DB-level COUNT, no Java loop
    @Query("""
            SELECT COUNT(cr) FROM CouponRequest cr
            WHERE cr.user.tenant.id = :tenantId
            AND cr.status = :status
            """)
    Long countByTenantIdAndStatus(
            @Param("tenantId") Long tenantId,
            @Param("status")   RequestStatus status
    );
}
