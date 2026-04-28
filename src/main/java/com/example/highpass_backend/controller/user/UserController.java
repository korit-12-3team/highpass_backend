package com.example.highpass_backend.controller.user;

import com.example.highpass_backend.config.CookieUtils;
import com.example.highpass_backend.dto.user.UpdatePasswordRequest;
import com.example.highpass_backend.dto.user.UpdateUserRequest;
import com.example.highpass_backend.dto.user.UserResponse;
import com.example.highpass_backend.dto.user.VerifyPasswordRequest;
import com.example.highpass_backend.security.CustomJwtPrincipal;
import com.example.highpass_backend.service.auth.RefreshTokenService;
import com.example.highpass_backend.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtils cookieUtils;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal CustomJwtPrincipal principal
    ) {
        return ResponseEntity.ok(userService.getUserById(principal.getUserId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long userId,
            @RequestBody UpdatePasswordRequest request
    ) {
        userService.updatePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/password/verify")
    public ResponseEntity<Void> verifyPassword(
            @PathVariable Long userId,
            @RequestBody VerifyPasswordRequest request
    ) {
        userService.verifyPassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> withdrawUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomJwtPrincipal principal,
            HttpServletResponse response
    ) {
        userService.withdrawUser(principal.getUserId(), userId);
        refreshTokenService.delete(principal.getUserId());
        cookieUtils.deleteAccessTokenCookie(response);
        cookieUtils.deleteRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }
}
