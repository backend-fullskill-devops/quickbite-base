package com.quickbite.user_service.models.dtos;

import com.quickbite.user_service.models.entities.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    @NotNull(message = "Full name is required")
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotNull(message = "Role is required")
    private Role role; // CUSTOMER, DRIVER, MERCHANT
}
