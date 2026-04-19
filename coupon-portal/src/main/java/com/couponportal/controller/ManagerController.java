package com.couponportal.controller;

import com.couponportal.dto.response.ApiResponse;
import com.couponportal.dto.response.CouponRequestResponse;
import com.couponportal.entity.User;
import com.couponportal.enums.RequestStatus;
import com.couponportal.service.CouponRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final CouponRequestService couponRequestService;

    // GET /api/manager/requests  — all requests in this tenant
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<CouponRequestResponse>>> getAllRequests(
            @AuthenticationPrincipal User user) {

        List<CouponRequestResponse> requests = couponRequestService
                .getAllRequestsForTenant(user.getTenant().getId());
        return ResponseEntity.ok(
                ApiResponse.success("All requests fetched", requests));
    }

    // GET /api/manager/requests?status=PENDING
    @GetMapping("/requests/filter")
    public ResponseEntity<ApiResponse<List<CouponRequestResponse>>> getRequestsByStatus(
            @RequestParam RequestStatus status,
            @AuthenticationPrincipal User user) {

        List<CouponRequestResponse> requests = couponRequestService
                .getRequestsByStatus(user.getTenant().getId(), status);
        return ResponseEntity.ok(
                ApiResponse.success("Filtered requests fetched", requests));
    }

    // PUT /api/manager/approve/{id}
    @PutMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<CouponRequestResponse>> approveRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        CouponRequestResponse response = couponRequestService
                .approveRequest(id, user.getTenant().getId());
        return ResponseEntity.ok(
                ApiResponse.success("Request approved successfully", response));
    }

    // PUT /api/manager/reject/{id}
    @PutMapping("/reject/{id}")
    public ResponseEntity<ApiResponse<CouponRequestResponse>> rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        CouponRequestResponse response = couponRequestService
                .rejectRequest(id, user.getTenant().getId());
        return ResponseEntity.ok(
                ApiResponse.success("Request rejected", response));
    }
}
