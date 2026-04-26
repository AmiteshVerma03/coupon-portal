package com.couponportal.service;

import com.couponportal.dto.request.CouponRequestDto;
import com.couponportal.dto.response.CouponRequestResponse;
import com.couponportal.enums.RequestStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CouponRequestService {

    // User: submit a new coupon request
    CouponRequestResponse submitRequest(CouponRequestDto dto, Long userId);

    // User: view their own requests — Task 1: paginated
    Page<CouponRequestResponse> getMyRequests(Long userId, int page, int size);

    // Manager/Admin: view all requests in their tenant — Task 1: paginated
    Page<CouponRequestResponse> getAllRequestsForTenant(Long tenantId, int page, int size);

    // Manager/Admin: view requests filtered by status — Task 1: paginated
    Page<CouponRequestResponse> getRequestsByStatus(Long tenantId, RequestStatus status, int page, int size);

    // Manager/Admin: approve a request
    CouponRequestResponse approveRequest(Long requestId, Long tenantId);

    // Manager/Admin: reject a request
    CouponRequestResponse rejectRequest(Long requestId, Long tenantId);

    // User: withdraw their own pending request
    void withdrawRequest(Long requestId, Long userId);
}
