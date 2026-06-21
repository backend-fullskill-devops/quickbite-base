package com.quickbite.user_service.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.quickbite.user_service.configs.principle.MyUserDetails;
import com.quickbite.user_service.models.dtos.ApiResponse;
import com.quickbite.user_service.models.dtos.UserAddressDto;
import com.quickbite.user_service.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAddressDto>>> getAddresses(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        return ResponseEntity.ok(ApiResponse.success(userService.getAddresses(userDetails.getUser().getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAddressDto>> addAddress(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @Valid @RequestBody UserAddressDto dto) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        return ResponseEntity.ok(ApiResponse.success(userService.addAddress(userDetails.getUser().getId(), dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserAddressDto>> updateAddress(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UserAddressDto dto) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        return ResponseEntity.ok(ApiResponse.success(userService.updateAddress(userDetails.getUser().getId(), id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable Long id) {
        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        userService.deleteAddress(userDetails.getUser().getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }
}
