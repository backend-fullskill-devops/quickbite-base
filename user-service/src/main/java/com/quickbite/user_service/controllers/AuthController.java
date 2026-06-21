package com.quickbite.user_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quickbite.user_service.models.dtos.ApiResponse;
import com.quickbite.user_service.models.dtos.JwtResponse;
import com.quickbite.user_service.models.dtos.LoginRequest;
import com.quickbite.user_service.models.dtos.TokenRefreshRequest;
import com.quickbite.user_service.models.dtos.UserRegisterRequest;
import com.quickbite.user_service.models.dtos.UserResponse;
import com.quickbite.user_service.services.RefreshTokenService;
import com.quickbite.user_service.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.registerUser(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.loginUser(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.success(refreshTokenService.refreshToken(request.getRefreshToken())));
    }
}
