package com.couponportal.service;

import com.couponportal.dto.response.NotificationResponse;
import com.couponportal.entity.User;
import com.couponportal.enums.NotificationType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {

    void sendNotification(User user, String message, NotificationType type);

    // Task 1: paginated notifications list
    Page<NotificationResponse> getMyNotifications(Long userId, int page, int size);

    List<NotificationResponse> getUnreadNotifications(Long userId);

    Long countUnread(Long userId);

    void markAllAsRead(Long userId);
}
