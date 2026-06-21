package com.quickbite.user_service.models.dtos;

import com.quickbite.user_service.models.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private final String type = "Bearer";
    private Role role;
}
