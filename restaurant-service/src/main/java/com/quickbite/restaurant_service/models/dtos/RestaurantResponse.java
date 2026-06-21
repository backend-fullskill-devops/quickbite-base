package com.quickbite.restaurant_service.models.dtos;

import lombok.Data;
import java.util.List;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private boolean isOpen;
    private List<MenuCategoryResponse> categories;
}
