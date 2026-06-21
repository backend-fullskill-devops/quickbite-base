package com.quickbite.user_service.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    @NotNull(message = "Refresh token is required")
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
