package com.couponportal.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCouponDto {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotBlank(message = "Platform is required")
    private String platform;

    @NotBlank(message = "Course name is required")
    private String course;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private int usageLimit;
}
