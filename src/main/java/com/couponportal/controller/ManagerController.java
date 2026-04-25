package com.couponportal.controller;

import com.couponportal.dto.response.ApiResponse;
import com.couponportal.dto.response.CouponRequestResponse;
import com.couponportal.entity.User;
import com.couponportal.enums.RequestStatus;
import com.couponportal.service.CouponRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final CouponRequestService couponRequestService;

    // Task 1 — paginated all-requests view
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

    // Task 1 — paginated status-filtered view
    @GetMapping("/requests/filter")
    public ResponseEntity<ApiResponse<Page<CouponRequestResponse>>> getRequestsByStatus(
            @RequestParam RequestStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Filtered requests fetched",
                        couponRequestService.getRequestsByStatus(
                                user.getTenant().getId(), status, page, size)));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<CouponRequestResponse>> approveRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Request approved successfully",
                        couponRequestService.approveRequest(id, user.getTenant().getId())));
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<ApiResponse<CouponRequestResponse>> rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Request rejected",
                        couponRequestService.rejectRequest(id, user.getTenant().getId())));
    }
}
