package com.quickbite.restaurant_service.models.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {
    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", message = "Base price must be greater than or equal to 0")
    private BigDecimal basePrice;

    private boolean isAvailable = true;
}
