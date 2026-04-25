package com.couponportal.entity;

import com.couponportal.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "coupon_request",
    indexes = {
        // Task 4 — matches V4__create_coupon_request.sql indexes
        @Index(name = "idx_request_user_id", columnList = "user_id"),
        @Index(name = "idx_request_status",  columnList = "status"),
        // Task 4 — composite index: manager dashboard filters by (tenant via user, status)
        // Most common query: findAllByTenantIdAndStatus — benefits from both columns indexed together
        @Index(name = "idx_request_user_status", columnList = "user_id, status")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CouponRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private String platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_coupon_id")
    private Coupon assignedCoupon;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
