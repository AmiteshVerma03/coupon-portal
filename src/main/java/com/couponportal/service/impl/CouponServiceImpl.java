package com.couponportal.service.impl;

import com.couponportal.dto.request.CreateCouponDto;
import com.couponportal.dto.response.CouponResponse;
import com.couponportal.entity.Coupon;
import com.couponportal.entity.Tenant;
import com.couponportal.exception.ResourceNotFoundException;
import com.couponportal.repository.CouponRepository;
import com.couponportal.repository.TenantRepository;
import com.couponportal.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public CouponResponse createCoupon(CreateCouponDto dto) {

        if (couponRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException(
                    "Coupon code already exists: " + dto.getCode());
        }

        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tenant not found with id: " + dto.getTenantId()));

        Coupon coupon = Coupon.builder()
                .code(dto.getCode())
                .platform(dto.getPlatform())
                .course(dto.getCourse())
                .expiryDate(dto.getExpiryDate())
                .usageLimit(dto.getUsageLimit())
                .usedCount(0)
                .tenant(tenant)
                .build();

        return mapToResponse(couponRepository.save(coupon));
    }

    // Task 1 — paginated admin coupon listing
    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponse> getAllCouponsForTenant(Long tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return couponRepository.findAllByTenantId(tenantId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAvailableCouponsForTenant(Long tenantId) {
        return couponRepository.findAllAvailableByTenantId(tenantId, LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long id) {
        return couponRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon not found with id: " + id));
        couponRepository.delete(coupon);
    }

    public CouponResponse mapToResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .platform(coupon.getPlatform())
                .course(coupon.getCourse())
                .expiryDate(coupon.getExpiryDate())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .remaining(coupon.getUsageLimit() - coupon.getUsedCount())
                .tenantId(coupon.getTenant().getId())
                .available(coupon.isAvailable())
                .build();
    }
}
