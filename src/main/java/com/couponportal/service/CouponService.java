package com.couponportal.service;

import com.couponportal.dto.request.CreateCouponDto;
import com.couponportal.dto.response.CouponResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CouponService {

    CouponResponse createCoupon(CreateCouponDto dto, Long tenantId);

    // Task 1: paginated for admin listing
    Page<CouponResponse> getAllCouponsForTenant(Long tenantId, int page, int size);

    // available coupons for users — typically small list, kept non-paginated
    List<CouponResponse> getAvailableCouponsForTenant(Long tenantId);

    CouponResponse getCouponById(Long id, Long tenantId);

    void deleteCoupon(Long id, Long tenantId);
}
