package com.couponportal.repository;

import com.couponportal.entity.CouponRequest;
import com.couponportal.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRequestRepository extends JpaRepository<CouponRequest, Long> {

    // all requests made by a specific user
    List<CouponRequest> findAllByUserId(Long userId);

    // all requests within a tenant (for manager view)
    @Query("""
            SELECT cr FROM CouponRequest cr
            WHERE cr.user.tenant.id = :tenantId
            ORDER BY cr.createdAt DESC
            """)
    List<CouponRequest> findAllByTenantId(@Param("tenantId") Long tenantId);

    // requests filtered by status within a tenant
    @Query("""
            SELECT cr FROM CouponRequest cr
            WHERE cr.user.tenant.id = :tenantId
            AND cr.status = :status
            ORDER BY cr.createdAt DESC
            """)
    List<CouponRequest> findAllByTenantIdAndStatus(
            @Param("tenantId") Long tenantId,
            @Param("status")   RequestStatus status
    );

    // count requests by status for analytics
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