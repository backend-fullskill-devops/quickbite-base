package com.quickbite.user_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quickbite.user_service.models.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByUserUsernameAndIsRevokedTrue(String username);
    Optional<RefreshToken> findByRefreshToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
}
