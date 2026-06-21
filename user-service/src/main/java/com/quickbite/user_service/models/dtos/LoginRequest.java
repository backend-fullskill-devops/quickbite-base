package com.quickbite.user_service.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password is required")
    private String password;
}
