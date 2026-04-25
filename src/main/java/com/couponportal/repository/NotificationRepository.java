package com.couponportal.repository;

import com.couponportal.entity.WebNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<WebNotification, Long> {

    // Task 1 — paginated: all notifications newest first
    Page<WebNotification> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Non-paginated (kept for internal use)
    List<WebNotification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // only unread notifications
    List<WebNotification> findAllByUserIdAndReadStatusFalse(Long userId);

    // Task 2 — DB-level COUNT — no Java loop
    Long countByUserIdAndReadStatusFalse(Long userId);

    // mark all notifications as read for a user
    @Modifying
    @Query("UPDATE WebNotification n SET n.readStatus = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
