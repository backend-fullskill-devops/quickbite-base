package com.quickbite.user_service.models.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddressDto {
    private Long id;
    private String label;
    private String detailAddress;
    private boolean isDefault;
}
