package com.quickbite.restaurant_service.models.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemResponse {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private boolean isAvailable;
}
