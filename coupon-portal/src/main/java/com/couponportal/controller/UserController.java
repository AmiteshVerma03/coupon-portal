package com.couponportal.controller;

import com.couponportal.dto.request.CouponRequestDto;
import com.couponportal.dto.response.ApiResponse;
import com.couponportal.dto.response.CouponRequestResponse;
import com.couponportal.dto.response.CouponResponse;
import com.couponportal.dto.response.NotificationResponse;
import com.couponportal.dto.response.UserResponse;
import com.couponportal.entity.User;
import com.couponportal.service.CouponRequestService;
import com.couponportal.service.CouponService;
import com.couponportal.service.NotificationService;
import com.couponportal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService          userService;
    private final CouponService        couponService;
    private final CouponRequestService couponRequestService;
    private final NotificationService  notificationService;

    // GET /api/user/profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {

        UserResponse response = userService.getMyProfile(user.getEmail());
        return ResponseEntity.ok(
                ApiResponse.success("Profile fetched", response));
    }

    // GET /api/user/courses  — view available coupons in their tenant
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAvailableCourses(
            @AuthenticationPrincipal User user) {

        List<CouponResponse> coupons = couponService
                .getAvailableCouponsForTenant(user.getTenant().getId());
        return ResponseEntity.ok(
                ApiResponse.success("Available courses fetched", coupons));
    }

    // POST /api/user/request-coupon
    @PostMapping("/request-coupon")
    public ResponseEntity<ApiResponse<CouponRequestResponse>> requestCoupon(
            @Valid @RequestBody CouponRequestDto dto,
            @AuthenticationPrincipal User user) {

        CouponRequestResponse response = couponRequestService
                .submitRequest(dto, user.getId());
        return ResponseEntity.ok(
                ApiResponse.success("Coupon request submitted", response));
    }

    // GET /api/user/my-requests
    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<CouponRequestResponse>>> getMyRequests(
            @AuthenticationPrincipal User user) {

        List<CouponRequestResponse> requests = couponRequestService
                .getMyRequests(user.getId());
        return ResponseEntity.ok(
                ApiResponse.success("Requests fetched", requests));
    }

    // GET /api/user/notifications
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user) {

        List<NotificationResponse> notifications = notificationService
                .getMyNotifications(user.getId());
        return ResponseEntity.ok(
                ApiResponse.success("Notifications fetched", notifications));
    }

    // GET /api/user/notifications/unread-count
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {

        Long count = notificationService.countUnread(user.getId());
        return ResponseEntity.ok(
                ApiResponse.success("Unread count", count));
    }

    // PUT /api/user/notifications/mark-read
    @PutMapping("/notifications/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User user) {

        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(
                ApiResponse.success("All notifications marked as read"));
    }
}
