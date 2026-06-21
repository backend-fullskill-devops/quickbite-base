package com.quickbite.user_service.configs.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.quickbite.user_service.configs.jwt.JwtUtils;
import com.quickbite.user_service.configs.principle.MyUserDetailsService;
import com.quickbite.user_service.repositories.RefreshTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final RefreshTokenRepository refreshTokenRepository;
    private final MyUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            if (token != null) {
                String username = jwtUtils.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtils.validateToken(token, userDetails)) {

                    boolean isRevoked = refreshTokenRepository.existsByUserUsernameAndIsRevokedTrue(userDetails.getUsername());

                    if (isRevoked) {
                        log.warn("Attempt to use a revoked Access Token: {}", username);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Phiên đăng nhập đã bị thu hồi hoặc đăng xuất.");
                        return; // stop processing
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Un Authentication {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}