package com.quickbite.restaurant_service.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantRequest {
    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    private boolean isOpen;
}
