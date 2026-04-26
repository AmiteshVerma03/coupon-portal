package com.couponportal.service.impl;

import com.couponportal.dto.response.UserResponse;
import com.couponportal.entity.User;
import com.couponportal.exception.ResourceNotFoundException;
import com.couponportal.exception.TenantMismatchException;
import com.couponportal.repository.RefreshTokenRepository;
import com.couponportal.repository.UserRepository;
import com.couponportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository         userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String email) {
        return mapToResponse(getUserByEmail(email));
    }

    // Task 1 — paginated admin user listing
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersInTenant(Long tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return userRepository.findAllByTenantId(tenantId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, Long tenantId) {
        User userToDelete = getUserById(userId);

        if (!userToDelete.getTenant().getId().equals(tenantId)) {
            throw new TenantMismatchException(
                    "You are not allowed to delete users outside your tenant");
        }

        refreshTokenRepository.deleteByUserId(userId);
        userRepository.delete(userToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .tenantId(user.getTenant().getId())
                .tenantName(user.getTenant().getName())
                .build();
    }
}
