package com.couponportal.dto.response;

import com.couponportal.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long             id;
    private String           message;
    private NotificationType type;
    private boolean          readStatus;
    private LocalDateTime    createdAt;
}
