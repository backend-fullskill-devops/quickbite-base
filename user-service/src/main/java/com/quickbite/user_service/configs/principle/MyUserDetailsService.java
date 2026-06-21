package com.quickbite.user_service.configs.principle;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.quickbite.user_service.models.entities.User;
import com.quickbite.user_service.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws RuntimeException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(username + " not found"));

        return MyUserDetails.builder()
                .user(user)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
