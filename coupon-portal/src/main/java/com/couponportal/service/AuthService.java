package com.couponportal.service;

import com.couponportal.dto.request.LoginRequest;
import com.couponportal.dto.request.RefreshTokenRequest;
import com.couponportal.dto.request.RegisterRequest;
import com.couponportal.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(Long userId);
}
