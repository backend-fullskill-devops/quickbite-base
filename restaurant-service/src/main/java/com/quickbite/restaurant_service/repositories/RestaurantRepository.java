package com.quickbite.restaurant_service.repositories;

import com.quickbite.restaurant_service.models.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByOwnerId(Long ownerId);

    List<Restaurant> findByIsOpen(boolean isOpen);
}
