package com.couponportal.dto.response;

import com.couponportal.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequestResponse {

    private Long          id;
    private Long          userId;
    private String        userName;
    private String        course;
    private String        platform;
    private RequestStatus status;
    private String        assignedCouponCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
