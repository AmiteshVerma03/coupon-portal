package com.couponportal.service;

import com.couponportal.dto.response.UserResponse;
import com.couponportal.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponse getMyProfile(String email);

    // Task 1: paginated user listing for admin
    Page<UserResponse> getAllUsersInTenant(Long tenantId, int page, int size);

    void deleteUser(Long userId, Long tenantId);

    User getUserById(Long userId);

    User getUserByEmail(String email);
}
