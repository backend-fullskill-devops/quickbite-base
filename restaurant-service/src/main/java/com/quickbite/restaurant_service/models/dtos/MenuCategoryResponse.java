package com.quickbite.restaurant_service.models.dtos;

import lombok.Data;
import java.util.List;

@Data
public class MenuCategoryResponse {
    private Long id;
    private String name;
    private List<MenuItemResponse> menuItems;
}
