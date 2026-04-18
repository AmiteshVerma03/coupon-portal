package com.couponportal.service;

import com.couponportal.dto.response.UserResponse;
import com.couponportal.entity.User;

import java.util.List;

public interface UserService {

    // Get currently logged-in user's profile
    UserResponse getMyProfile(String email);

    // Admin: get all users in a tenant
    List<UserResponse> getAllUsersInTenant(Long tenantId);

    // Admin: delete a user
    void deleteUser(Long userId);

    // Helper used internally by services
    User getUserById(Long userId);

    User getUserByEmail(String email);
}
