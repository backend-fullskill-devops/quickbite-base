package com.quickbite.user_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.quickbite.user_service.configs.jwt.JwtUtils;
import com.quickbite.user_service.models.dtos.JwtResponse;
import com.quickbite.user_service.models.entities.RefreshToken;
import com.quickbite.user_service.models.entities.User;
import com.quickbite.user_service.repositories.RefreshTokenRepository;
import com.quickbite.user_service.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @Value("${jwt.expired.refresh}")
    private Long dayRefreshToken;

    public JwtResponse refreshToken(String refreshToken) {
        RefreshToken refresh = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token. Please login again."));

        if (refresh.getIsRevoked()) {
            throw new RuntimeException("Invalid refresh token. Please login again.");
        }

        if (refresh.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refresh);
            throw new RuntimeException("Invalid refresh token. Please login again.");
        }

        User user = userRepository.findById(refresh.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token. Please login again."));

        return JwtResponse.builder()
                .accessToken(jwtUtils.generateAccessToken(refresh.getUser()))
                .refreshToken(refreshToken)
                .role(refresh.getUser().getRole())
                .build();
    }

    public String createRefreshToken(User user) {

        Optional<RefreshToken> optional = refreshTokenRepository.findByUserId(user.getId());

        if (optional.isPresent()) {
            return optional.get().getRefreshToken();
        }

        // 1. Sinh chuỗi token ngẫu nhiên, an toàn (Không dùng JWT cho Refresh Token)
        String refreshTokenString = UUID.randomUUID().toString();

        // 2. Khởi tạo thực thể lưu trữ phiên đăng nhập
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshTokenString)
                .isRevoked(false)
                .expiresAt(LocalDateTime.now().plusDays(dayRefreshToken))
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshTokenString;
    }

    public void revokedRefreshToken(Long userId) {
        refreshTokenRepository.findByUserId(userId).ifPresent(session -> {

            session.setIsRevoked(true);
            refreshTokenRepository.save(session);
        });
    }
}
