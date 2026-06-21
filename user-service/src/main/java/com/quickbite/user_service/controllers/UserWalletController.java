package com.quickbite.user_service.controllers;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.quickbite.user_service.configs.principle.MyUserDetails;
import com.quickbite.user_service.models.dtos.ApiResponse;
import com.quickbite.user_service.models.dtos.UserWalletDto;
import com.quickbite.user_service.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/wallet")
@RequiredArgsConstructor
public class UserWalletController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserWalletDto>> getWallet(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        return ResponseEntity.ok(ApiResponse.success(userService.getWallet(userDetails.getUser().getId())));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<UserWalletDto>> deposit(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @RequestParam BigDecimal amount) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        return ResponseEntity.ok(ApiResponse.success(userService.depositMoney(userDetails.getUser().getId(), amount)));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<UserWalletDto>> withdraw(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @RequestParam BigDecimal amount) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        return ResponseEntity.ok(ApiResponse.success(userService.withdrawMoney(userDetails.getUser().getId(), amount)));
    }
}
