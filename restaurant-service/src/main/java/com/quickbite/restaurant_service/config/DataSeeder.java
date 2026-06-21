package com.quickbite.restaurant_service.config;

import com.quickbite.restaurant_service.models.entities.MenuCategory;
import com.quickbite.restaurant_service.models.entities.MenuItem;
import com.quickbite.restaurant_service.models.entities.Restaurant;
import com.quickbite.restaurant_service.repositories.MenuCategoryRepository;
import com.quickbite.restaurant_service.repositories.MenuItemRepository;
import com.quickbite.restaurant_service.repositories.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (restaurantRepository.count() == 0) {
            // Seed Restaurant 1
            Restaurant pizzaShop = new Restaurant();
            pizzaShop.setName("Gourmet Pizza Place");
            pizzaShop.setOwnerId(2L); // UserId 2 is Merchant
            pizzaShop.setOpen(true);
            pizzaShop.setCategories(new ArrayList<>());
            pizzaShop = restaurantRepository.save(pizzaShop);

            // Seed Categories for Restaurant 1
            MenuCategory pizzas = new MenuCategory();
            pizzas.setName("Pizzas");
            pizzas.setRestaurant(pizzaShop);
            pizzas.setMenuItems(new ArrayList<>());
            pizzas = menuCategoryRepository.save(pizzas);

            MenuCategory drinks = new MenuCategory();
            drinks.setName("Drinks");
            drinks.setRestaurant(pizzaShop);
            drinks.setMenuItems(new ArrayList<>());
            drinks = menuCategoryRepository.save(drinks);

            // Seed Menu Items for Pizzas
            MenuItem margherita = new MenuItem();
            margherita.setName("Margherita Pizza");
            margherita.setBasePrice(new BigDecimal("9.99"));
            margherita.setAvailable(true);
            margherita.setCategory(pizzas);

            MenuItem pepperoni = new MenuItem();
            pepperoni.setName("Pepperoni Pizza");
            pepperoni.setBasePrice(new BigDecimal("12.99"));
            pepperoni.setAvailable(true);
            pepperoni.setCategory(pizzas);

            menuItemRepository.saveAll(Arrays.asList(margherita, pepperoni));

            // Seed Menu Items for Drinks
            MenuItem cola = new MenuItem();
            cola.setName("Coca-Cola");
            cola.setBasePrice(new BigDecimal("1.99"));
            cola.setAvailable(true);
            cola.setCategory(drinks);

            MenuItem water = new MenuItem();
            water.setName("Mineral Water");
            water.setBasePrice(new BigDecimal("0.99"));
            water.setAvailable(true);
            water.setCategory(drinks);

            menuItemRepository.saveAll(Arrays.asList(cola, water));

            System.out.println(">>> Seeded initial restaurant and menu items successfully!");
        }
    }
}
