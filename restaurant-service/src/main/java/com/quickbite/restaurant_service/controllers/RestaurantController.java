package com.quickbite.restaurant_service.controllers;

import com.quickbite.restaurant_service.models.dtos.*;
import com.quickbite.restaurant_service.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
            @Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse response = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAllRestaurants(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Boolean isOpen) {
        List<RestaurantResponse> response = restaurantService.getAllRestaurants(ownerId, isOpen);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateRestaurantStatus(
            @PathVariable Long id,
            @RequestParam boolean isOpen) {
        RestaurantResponse response = restaurantService.updateRestaurantStatus(id, isOpen);
        return ResponseEntity.ok(ApiResponse.success("Restaurant status updated successfully", response));
    }

    @PostMapping("/{id}/categories")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> addCategory(
            @PathVariable Long id,
            @Valid @RequestBody MenuCategoryRequest request) {
        MenuCategoryResponse response = restaurantService.addCategory(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category added successfully", response));
    }

    @PostMapping("/categories/{categoryId}/items")
    public ResponseEntity<ApiResponse<MenuItemResponse>> addMenuItem(
            @PathVariable Long categoryId,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse response = restaurantService.addMenuItem(categoryId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Menu item added successfully", response));
    }
}
