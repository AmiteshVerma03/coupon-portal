package com.couponportal.service;

import com.couponportal.dto.response.NotificationResponse;
import com.couponportal.entity.User;
import com.couponportal.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    // Create and save a notification for a user
    void sendNotification(User user, String message, NotificationType type);

    // Get all notifications for a user
    List<NotificationResponse> getMyNotifications(Long userId);

    // Get only unread notifications
    List<NotificationResponse> getUnreadNotifications(Long userId);

    // Count unread notifications
    Long countUnread(Long userId);

    // Mark all notifications as read
    void markAllAsRead(Long userId);
}
