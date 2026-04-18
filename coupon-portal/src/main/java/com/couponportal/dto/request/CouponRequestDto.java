package com.couponportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CouponRequestDto {

    @NotBlank(message = "Course name is required")
    private String course;

    @NotBlank(message = "Platform name is required")
    private String platform;
}
