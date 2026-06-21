package com.quickbite.restaurant_service.repositories;

import com.quickbite.restaurant_service.models.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryId(Long categoryId);
}
