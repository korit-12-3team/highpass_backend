package com.example.highpass_backend.controller;

import com.example.highpass_backend.dto.ApiResponse;
import com.example.highpass_backend.entity.user.RefreshToken;
import com.example.highpass_backend.security.CookieUtils;
import com.example.highpass_backend.security.JwtProperties;
import com.example.highpass_backend.security.JwtTokenProvider;
import com.example.highpass_backend.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final CookieUtils cookieUtils;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieUtils.getCookieValue(request, "refresh_token");

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
        }

        if (!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new IllegalArgumentException("잘못된 리프레시 토큰입니다.");
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        RefreshToken saved = refreshTokenService.getByUserId(userId);

        if (!saved.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(userId, null);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        refreshTokenService.save(
                userId,
                newRefreshToken,
                LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000)
        );

        cookieUtils.addAccessTokenCookie(response, accessToken);
        cookieUtils.addRefreshTokenCookie(response, newRefreshToken);

        return ResponseEntity.ok(new ApiResponse("토큰 재발급 완료"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieUtils.getCookieValue(request, "refresh_token");

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            refreshTokenService.delete(userId);
        }

        cookieUtils.deleteAccessTokenCookie(response);
        cookieUtils.deleteRefreshTokenCookie(response);

        return ResponseEntity.ok(new ApiResponse("로그아웃 완료"));
    }
}