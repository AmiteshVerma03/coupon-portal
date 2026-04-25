package com.couponportal.controller;

import com.couponportal.dto.request.CreateCouponDto;
import com.couponportal.dto.response.ApiResponse;
import com.couponportal.dto.response.CouponRequestResponse;
import com.couponportal.dto.response.CouponResponse;
import com.couponportal.dto.response.UserResponse;
import com.couponportal.entity.User;
import com.couponportal.service.CouponRequestService;
import com.couponportal.service.CouponService;
import com.couponportal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CouponService        couponService;
    private final UserService          userService;
    private final CouponRequestService couponRequestService;

    // ── Coupon Management ───────────────────────────────────

    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
            @Valid @RequestBody CreateCouponDto dto) {

        return ResponseEntity.ok(
                ApiResponse.success("Coupon created successfully",
                        couponService.createCoupon(dto)));
    }

    // Task 1 — paginated coupon list
    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<Page<CouponResponse>>> getAllCoupons(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success("All coupons fetched",
                        couponService.getAllCouponsForTenant(
                                user.getTenant().getId(), page, size)));
    }

    @GetMapping("/coupon/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success("Coupon fetched",
                        couponService.getCouponById(id)));
    }

    @DeleteMapping("/coupon/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @PathVariable Long id) {

        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted successfully"));
    }

    // ── User Management ─────────────────────────────────────

    // Task 1 — paginated user list
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success("All users fetched",
                        userService.getAllUsersInTenant(
                                user.getTenant().getId(), page, size)));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    // ── Requests ────────────────────────────────────────────

    // Task 1 — paginated requests list
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<Page<CouponRequestResponse>>> getAllRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success("All requests fetched",
                        couponRequestService.getAllRequestsForTenant(
                                user.getTenant().getId(), page, size)));
    }
}
