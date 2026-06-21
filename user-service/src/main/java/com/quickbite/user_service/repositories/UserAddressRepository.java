package com.quickbite.user_service.repositories;

import com.quickbite.user_service.models.entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserId(Long userId);
}
