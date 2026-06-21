package com.quickbite.user_service.repositories;

import com.quickbite.user_service.models.entities.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
    Optional<UserWallet> findByUserId(Long userId);
}
