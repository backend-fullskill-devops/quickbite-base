package com.quickbite.user_service.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quickbite.user_service.configs.jwt.JwtUtils;
import com.quickbite.user_service.models.dtos.JwtResponse;
import com.quickbite.user_service.models.dtos.LoginRequest;
import com.quickbite.user_service.models.dtos.UserAddressDto;
import com.quickbite.user_service.models.dtos.UserRegisterRequest;
import com.quickbite.user_service.models.dtos.UserResponse;
import com.quickbite.user_service.models.dtos.UserUpdateRequest;
import com.quickbite.user_service.models.dtos.UserWalletDto;
import com.quickbite.user_service.models.entities.User;
import com.quickbite.user_service.models.entities.UserAddress;
import com.quickbite.user_service.models.entities.UserWallet;
import com.quickbite.user_service.repositories.UserAddressRepository;
import com.quickbite.user_service.repositories.UserRepository;
import com.quickbite.user_service.repositories.UserWalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final UserWalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .hashedPassword(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        UserWallet wallet = UserWallet.builder()
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        user.setWallet(wallet);

        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    @Transactional
    public JwtResponse loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getHashedPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .build();
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Transactional
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName().isPresent()) {
            user.setFullName(request.getFullName().get());
        }

        if (request.getUsername().isPresent()) {
            String newUsername = request.getUsername().get();
            if (!newUsername.equals(user.getUsername()) && userRepository.findByUsername(newUsername).isPresent()) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(newUsername);
        }

        if (request.getPassword().isPresent()) {
            user.setHashedPassword(passwordEncoder.encode(request.getPassword().get()));
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    // --- UserAddress Logic ---

    @Transactional
    public List<UserAddressDto> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToAddressDto)
                .toList();
    }

    @Transactional
    public UserAddressDto addAddress(Long userId, UserAddressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.isDefault()) {
            resetDefaultAddresses(userId);
        }

        UserAddress address = UserAddress.builder()
                .label(dto.getLabel())
                .detailAddress(dto.getDetailAddress())
                .isDefault(dto.isDefault())
                .user(user)
                .build();

        UserAddress savedAddress = addressRepository.save(address);
        return mapToAddressDto(savedAddress);
    }

    @Transactional
    public UserAddressDto updateAddress(Long userId, Long addressId, UserAddressDto dto) {
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this address");
        }

        if (dto.isDefault() && !address.isDefault()) {
            resetDefaultAddresses(userId);
        }

        address.setLabel(dto.getLabel());
        address.setDetailAddress(dto.getDetailAddress());
        address.setDefault(dto.isDefault());

        UserAddress updatedAddress = addressRepository.save(address);
        return mapToAddressDto(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this address");
        }

        addressRepository.delete(address);
    }

    private void resetDefaultAddresses(Long userId) {
        List<UserAddress> addresses = addressRepository.findByUserId(userId);
        for (UserAddress addr : addresses) {
            if (addr.isDefault()) {
                addr.setDefault(false);
                addressRepository.save(addr);
            }
        }
    }

    // --- UserWallet Logic ---

    @Transactional(readOnly = true)
    public UserWalletDto getWallet(Long userId) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return mapToWalletDto(wallet);
    }

    @Transactional
    public UserWalletDto depositMoney(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        UserWallet savedWallet = walletRepository.save(wallet);
        return mapToWalletDto(savedWallet);
    }

    @Transactional
    public UserWalletDto withdrawMoney(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }

        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        UserWallet savedWallet = walletRepository.save(wallet);
        return mapToWalletDto(savedWallet);
    }

    // --- Helpers ---

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    private UserAddressDto mapToAddressDto(UserAddress address) {
        return UserAddressDto.builder()
                .id(address.getId())
                .label(address.getLabel())
                .detailAddress(address.getDetailAddress())
                .isDefault(address.isDefault())
                .build();
    }

    private UserWalletDto mapToWalletDto(UserWallet wallet) {
        return UserWalletDto.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }
}
