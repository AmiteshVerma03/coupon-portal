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
import org.springframework.data.domain.Page;
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

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Profile fetched",
                        userService.getMyProfile(user.getEmail())));
    }

    // available coupons — small list, non-paginated is fine
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAvailableCourses(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Available courses fetched",
                        couponService.getAvailableCouponsForTenant(user.getTenant().getId())));
    }

    @PostMapping("/request-coupon")
    public ResponseEntity<ApiResponse<CouponRequestResponse>> requestCoupon(
            @Valid @RequestBody CouponRequestDto dto,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Coupon request submitted",
                        couponRequestService.submitRequest(dto, user.getId())));
    }

    @DeleteMapping("/request-coupon/{id}")
    public ResponseEntity<ApiResponse<Void>> withdrawCouponRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        couponRequestService.withdrawRequest(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Coupon request withdrawn"));
    }

    // Task 1 — paginated: user's own requests
    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<Page<CouponRequestResponse>>> getMyRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success("Requests fetched",
                        couponRequestService.getMyRequests(user.getId(), page, size)));
    }

    // Task 1 — paginated: notification list
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                ApiResponse.success("Notifications fetched",
                        notificationService.getMyNotifications(user.getId(), page, size)));
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Unread count",
                        notificationService.countUnread(user.getId())));
    }

    @PutMapping("/notifications/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User user) {

        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
