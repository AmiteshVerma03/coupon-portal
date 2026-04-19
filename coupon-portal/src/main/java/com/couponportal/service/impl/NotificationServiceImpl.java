package com.couponportal.service.impl;

import com.couponportal.dto.response.NotificationResponse;
import com.couponportal.entity.WebNotification;
import com.couponportal.entity.User;
import com.couponportal.enums.NotificationType;
import com.couponportal.repository.NotificationRepository;
import com.couponportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void sendNotification(User user, String message, NotificationType type) {
        WebNotification notification = WebNotification.builder()
                .user(user)
                .message(message)
                .type(type)
                .readStatus(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification sent to user {}: {}", user.getEmail(), message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository
                .findAllByUserIdAndReadStatusFalse(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndReadStatusFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    // ── Mapper ──────────────────────────────────────────────

   private NotificationResponse mapToResponse(WebNotification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .readStatus(n.getReadStatus())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
