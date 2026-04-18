package com.couponportal.service;

import com.couponportal.dto.request.CreateCouponDto;
import com.couponportal.dto.response.CouponResponse;

import java.util.List;

public interface CouponService {

    CouponResponse createCoupon(CreateCouponDto dto);

    List<CouponResponse> getAllCouponsForTenant(Long tenantId);

    List<CouponResponse> getAvailableCouponsForTenant(Long tenantId);

    CouponResponse getCouponById(Long id);

    void deleteCoupon(Long id);
}
