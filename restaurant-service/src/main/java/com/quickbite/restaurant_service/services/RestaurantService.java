package com.quickbite.restaurant_service.services;

import com.quickbite.restaurant_service.models.dtos.*;
import com.quickbite.restaurant_service.models.entities.MenuCategory;
import com.quickbite.restaurant_service.models.entities.MenuItem;
import com.quickbite.restaurant_service.models.entities.Restaurant;
import com.quickbite.restaurant_service.repositories.MenuCategoryRepository;
import com.quickbite.restaurant_service.repositories.MenuItemRepository;
import com.quickbite.restaurant_service.repositories.RestaurantRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setOwnerId(request.getOwnerId());
        restaurant.setOpen(request.isOpen());
        restaurant.setCategories(new ArrayList<>());

        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToRestaurantResponse(saved);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        return mapToRestaurantResponse(restaurant);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants(Long ownerId, Boolean isOpen) {
        List<Restaurant> restaurants;
        if (ownerId != null) {
            restaurants = restaurantRepository.findByOwnerId(ownerId);
            if (isOpen != null) {
                restaurants = restaurants.stream()
                        .filter(r -> r.isOpen() == isOpen)
                        .collect(Collectors.toList());
            }
        } else if (isOpen != null) {
            restaurants = restaurantRepository.findByIsOpen(isOpen);
        } else {
            restaurants = restaurantRepository.findAll();
        }
        return restaurants.stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    public MenuCategoryResponse addCategory(Long restaurantId, MenuCategoryRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        MenuCategory category = new MenuCategory();
        category.setName(request.getName());
        category.setRestaurant(restaurant);
        category.setMenuItems(new ArrayList<>());

        MenuCategory saved = menuCategoryRepository.save(category);
        return mapToMenuCategoryResponse(saved);
    }

    public MenuItemResponse addMenuItem(Long categoryId, MenuItemRequest request) {
        MenuCategory category = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Menu category not found with id: " + categoryId));

        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setBasePrice(request.getBasePrice());
        menuItem.setAvailable(request.isAvailable());
        menuItem.setCategory(category);

        MenuItem saved = menuItemRepository.save(menuItem);
        return mapToMenuItemResponse(saved);
    }

    public RestaurantResponse updateRestaurantStatus(Long restaurantId, boolean isOpen) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        restaurant.setOpen(isOpen);
        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToRestaurantResponse(saved);
    }

    private RestaurantResponse mapToRestaurantResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setOwnerId(restaurant.getOwnerId());
        response.setOpen(restaurant.isOpen());
        if (restaurant.getCategories() != null) {
            response.setCategories(restaurant.getCategories().stream()
                    .map(this::mapToMenuCategoryResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setCategories(new ArrayList<>());
        }
        return response;
    }

    private MenuCategoryResponse mapToMenuCategoryResponse(MenuCategory category) {
        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        if (category.getMenuItems() != null) {
            response.setMenuItems(category.getMenuItems().stream()
                    .map(this::mapToMenuItemResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setMenuItems(new ArrayList<>());
        }
        return response;
    }

    private MenuItemResponse mapToMenuItemResponse(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setBasePrice(item.getBasePrice());
        response.setAvailable(item.isAvailable());
        return response;
    }
}
