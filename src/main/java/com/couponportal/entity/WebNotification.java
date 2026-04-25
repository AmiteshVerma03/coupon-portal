package com.couponportal.entity;

import com.couponportal.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "notification",
    indexes = {
        // Task 4 — matches V5__create_notification.sql indexes
        @Index(name = "idx_notification_user_id",     columnList = "user_id"),
        @Index(name = "idx_notification_read_status", columnList = "read_status"),
        // Task 4 — composite: countByUserIdAndReadStatusFalse and findAllByUserIdAndReadStatusFalse
        // both filter on (user_id, read_status) — single index covers both
        @Index(name = "idx_notification_user_read",   columnList = "user_id, read_status")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WebNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "read_status", nullable = false)
    @Builder.Default
    private Boolean readStatus = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
