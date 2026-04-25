package com.couponportal.service.impl;

import com.couponportal.dto.request.CouponRequestDto;
import com.couponportal.dto.response.CouponRequestResponse;
import com.couponportal.entity.Coupon;
import com.couponportal.entity.CouponRequest;
import com.couponportal.entity.User;
import com.couponportal.enums.NotificationType;
import com.couponportal.enums.RequestStatus;
import com.couponportal.exception.ResourceNotFoundException;
import com.couponportal.exception.TenantMismatchException;
import com.couponportal.repository.CouponRepository;
import com.couponportal.repository.CouponRequestRepository;
import com.couponportal.repository.UserRepository;
import com.couponportal.service.CouponRequestService;
import com.couponportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRequestServiceImpl implements CouponRequestService {

    private final CouponRequestRepository couponRequestRepository;
    private final CouponRepository        couponRepository;
    private final UserRepository          userRepository;
    private final NotificationService     notificationService;

    @Override
    @Transactional
    public CouponRequestResponse submitRequest(CouponRequestDto dto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        Long tenantId = user.getTenant().getId();

        CouponRequest couponRequest = CouponRequest.builder()
                .user(user)
                .course(dto.getCourse())
                .platform(dto.getPlatform())
                .status(RequestStatus.PENDING)
                .build();

        notificationService.sendNotification(
                user,
                "Your coupon request for '" + dto.getCourse() +
                        "' is pending review by your manager/admin.",
                NotificationType.GENERAL
        );

        // Task 2 — DB-limited: fetch only 3 recommendations directly from DB
        // Previously: findAllAvailableByTenantId loaded ALL coupons, then Java did .limit(3)
        // Now: Pageable.ofSize(3) pushes the LIMIT into the SQL query
        List<Coupon> topRecommendations = couponRepository
                .findTopAvailableByTenantId(tenantId, LocalDate.now(), PageRequest.of(0, 3));

        if (!topRecommendations.isEmpty()) {
            String recommendations = topRecommendations.stream()
                    .map(c -> c.getCourse() + " (" + c.getPlatform() + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            notificationService.sendNotification(
                    user,
                    "While you wait, these courses have available coupons: " + recommendations,
                    NotificationType.RECOMMENDATION
            );
        }

        log.info("Coupon request submitted as pending for user {} for course {}",
                user.getEmail(), dto.getCourse());

        return mapToResponse(couponRequestRepository.save(couponRequest));
    }

    // Task 1: paginated
    @Override
    @Transactional(readOnly = true)
    public Page<CouponRequestResponse> getMyRequests(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return couponRequestRepository.findAllByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    // Task 1: paginated
    @Override
    @Transactional(readOnly = true)
    public Page<CouponRequestResponse> getAllRequestsForTenant(Long tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return couponRequestRepository.findAllByTenantId(tenantId, pageable)
                .map(this::mapToResponse);
    }

    // Task 1: paginated
    @Override
    @Transactional(readOnly = true)
    public Page<CouponRequestResponse> getRequestsByStatus(
            Long tenantId, RequestStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return couponRequestRepository.findAllByTenantIdAndStatus(tenantId, status, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public CouponRequestResponse approveRequest(Long requestId, Long tenantId) {

        CouponRequest request = getValidatedRequest(requestId, tenantId);

        Coupon coupon = couponRepository.findAvailableCoupon(
                        tenantId, request.getCourse(), request.getPlatform(), LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No available coupon found for: " + request.getCourse()));

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        request.setAssignedCoupon(coupon);
        request.setStatus(RequestStatus.APPROVED);

        notificationService.sendNotification(
                request.getUser(),
                "Your coupon request for '" + request.getCourse() +
                        "' has been approved! Coupon code: " + coupon.getCode(),
                NotificationType.REQUEST_APPROVED
        );

        log.info("Request {} approved, coupon {} assigned", requestId, coupon.getCode());
        return mapToResponse(couponRequestRepository.save(request));
    }

    @Override
    @Transactional
    public CouponRequestResponse rejectRequest(Long requestId, Long tenantId) {

        CouponRequest request = getValidatedRequest(requestId, tenantId);
        request.setStatus(RequestStatus.REJECTED);

        notificationService.sendNotification(
                request.getUser(),
                "Your coupon request for '" + request.getCourse() +
                        "' has been rejected. Please contact your manager for more information.",
                NotificationType.REQUEST_REJECTED
        );

        log.info("Request {} rejected", requestId);
        return mapToResponse(couponRequestRepository.save(request));
    }

    private CouponRequest getValidatedRequest(Long requestId, Long tenantId) {
        CouponRequest request = couponRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request not found with id: " + requestId));

        if (!request.getUser().getTenant().getId().equals(tenantId)) {
            throw new TenantMismatchException(
                    "This request does not belong to your organization");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Request is already " + request.getStatus());
        }

        return request;
    }

    private CouponRequestResponse mapToResponse(CouponRequest r) {
        return CouponRequestResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .course(r.getCourse())
                .platform(r.getPlatform())
                .status(r.getStatus())
                .assignedCouponCode(
                        r.getAssignedCoupon() != null
                                ? r.getAssignedCoupon().getCode()
                                : null)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
