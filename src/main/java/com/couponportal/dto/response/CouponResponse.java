package com.couponportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {

    private Long      id;
    private String    code;
    private String    platform;
    private String    course;
    private LocalDate expiryDate;
    private int       usageLimit;
    private int       usedCount;
    private int       remaining;
    private Long      tenantId;
    private boolean   available;
}
