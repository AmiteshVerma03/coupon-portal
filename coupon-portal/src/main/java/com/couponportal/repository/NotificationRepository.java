package com.couponportal.repository;

import com.couponportal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // all notifications for a user, newest first
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // only unread notifications
    List<Notification> findAllByUserIdAndReadStatusFalse(Long userId);

    // count unread notifications
    Long countByUserIdAndReadStatusFalse(Long userId);

    // mark all notifications as read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.readStatus = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}