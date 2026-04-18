package com.couponportal.service;

import com.couponportal.dto.request.CouponRequestDto;
import com.couponportal.dto.response.CouponRequestResponse;
import com.couponportal.enums.RequestStatus;

import java.util.List;

public interface CouponRequestService {

    // User: submit a new coupon request
    CouponRequestResponse submitRequest(CouponRequestDto dto, Long userId);

    // User: view their own requests
    List<CouponRequestResponse> getMyRequests(Long userId);

    // Manager/Admin: view all requests in their tenant
    List<CouponRequestResponse> getAllRequestsForTenant(Long tenantId);

    // Manager/Admin: view requests filtered by status
    List<CouponRequestResponse> getRequestsByStatus(Long tenantId, RequestStatus status);

    // Manager/Admin: approve a request
    CouponRequestResponse approveRequest(Long requestId, Long tenantId);

    // Manager/Admin: reject a request
    CouponRequestResponse rejectRequest(Long requestId, Long tenantId);
}
