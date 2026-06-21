package com.quickbite.restaurant_service.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
}
