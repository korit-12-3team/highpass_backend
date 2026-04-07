package com.example.highpass_backend.security;

import com.example.highpass_backend.repository.user.UserRepository;
import com.example.highpass_backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtils cookieUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = cookieUtils.getCookieValue(request, "access_token");

        if (token != null && jwtTokenProvider.validateToken(token)) {
            if ("access".equals(jwtTokenProvider.getTokenType(token))) {
                SecurityContextHolder.getContext()
                        .setAuthentication(jwtTokenProvider.getAuthentication(token));
            }
        }

        filterChain.doFilter(request, response);
    }
}