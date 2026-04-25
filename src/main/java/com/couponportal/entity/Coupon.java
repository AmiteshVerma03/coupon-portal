package com.couponportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "coupon",
    indexes = {
        // Task 4 — match indexes already in V3__create_coupon.sql
        // All three are used in findAvailableCoupon / findAllAvailableByTenantId queries
        @Index(name = "idx_coupon_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_coupon_platform",  columnList = "platform"),
        @Index(name = "idx_coupon_course",    columnList = "course")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String platform;

    @Column(nullable = false)
    private String course;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // ── Helper ─────────────────────────────────────────────

    public boolean isAvailable() {
        return usedCount < usageLimit && !expiryDate.isBefore(LocalDate.now());
    }
}
