package com.quickbite.user_service.configs;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.quickbite.user_service.models.entities.Role;
import com.quickbite.user_service.models.entities.User;
import com.quickbite.user_service.models.entities.UserAddress;
import com.quickbite.user_service.models.entities.UserWallet;
import com.quickbite.user_service.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor    
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String hashedPassword = passwordEncoder.encode("12345678");
        if (userRepository.count() == 0) {
            User user1 = User.builder()
                    .fullName("QuickBite Customer")
                    .username("customer")
                    .role(Role.CUSTOMER)
                    .hashedPassword(hashedPassword)
                    .build();

            UserAddress address1 = UserAddress.builder()
                    .label("Nhà riêng")
                    .detailAddress("123 Phố Huế, Hai Bà Trưng, Hà Nội")
                    .isDefault(true)
                    .user(user1)
                    .build();

            UserWallet wallet1 = UserWallet.builder()
                    .balance(new BigDecimal("1000000.00")) // 1,000,000 VND
                    .user(user1)
                    .build();

            user1.setAddresses(List.of(address1));
            user1.setWallet(wallet1);

            User user2 = User.builder()
                    .fullName("QuickBite Merchant")
                    .username("merchant")
                    .role(Role.MERCHANT)
                    .hashedPassword(hashedPassword)
                    .build();

            UserWallet wallet2 = UserWallet.builder()
                    .balance(new BigDecimal("0.00"))
                    .user(user2)
                    .build();

            user2.setWallet(wallet2);

            User user3 = User.builder()
                    .fullName("QuickBite Driver")
                    .username("driver")
                    .role(Role.DRIVER)
                    .hashedPassword(hashedPassword)
                    .build();

            UserWallet wallet3 = UserWallet.builder()
                    .balance(new BigDecimal("50000.00"))
                    .user(user3)
                    .build();

            user3.setWallet(wallet3);

            userRepository.saveAll(List.of(user1, user2, user3));
        }
    }
}
