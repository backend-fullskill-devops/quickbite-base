package com.quickbite.restaurant_service.repositories;

import com.quickbite.restaurant_service.models.entities.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    List<MenuCategory> findByRestaurantId(Long restaurantId);
}
