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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CouponService        couponService;
    private final UserService          userService;
    private final CouponRequestService couponRequestService;

    // ── Coupon Management ───────────────────────────────────

    // POST /api/admin/coupon
    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
            @Valid @RequestBody CreateCouponDto dto) {

        CouponResponse response = couponService.createCoupon(dto);
        return ResponseEntity.ok(
                ApiResponse.success("Coupon created successfully", response));
    }

    // GET /api/admin/coupons  — all coupons in admin's tenant
    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons(
            @AuthenticationPrincipal User user) {

        List<CouponResponse> coupons = couponService
                .getAllCouponsForTenant(user.getTenant().getId());
        return ResponseEntity.ok(
                ApiResponse.success("All coupons fetched", coupons));
    }

    // GET /api/admin/coupon/{id}
    @GetMapping("/coupon/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(
            @PathVariable Long id) {

        CouponResponse response = couponService.getCouponById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Coupon fetched", response));
    }

    // DELETE /api/admin/coupon/{id}
    @DeleteMapping("/coupon/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @PathVariable Long id) {

        couponService.deleteCoupon(id);
        return ResponseEntity.ok(
                ApiResponse.success("Coupon deleted successfully"));
    }

    // ── User Management ─────────────────────────────────────

    // GET /api/admin/users  — all users in admin's tenant
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @AuthenticationPrincipal User user) {

        List<UserResponse> users = userService
                .getAllUsersInTenant(user.getTenant().getId());
        return ResponseEntity.ok(
                ApiResponse.success("All users fetched", users));
    }

    // DELETE /api/admin/user/{id}
    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully"));
    }

    // ── Analytics ───────────────────────────────────────────

    // GET /api/admin/requests  — all requests in tenant
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<CouponRequestResponse>>> getAllRequests(
            @AuthenticationPrincipal User user) {

        List<CouponRequestResponse> requests = couponRequestService
                .getAllRequestsForTenant(user.getTenant().getId());
        return ResponseEntity.ok(
                ApiResponse.success("All requests fetched", requests));
    }
}
