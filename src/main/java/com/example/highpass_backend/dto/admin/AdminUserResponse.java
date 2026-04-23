package com.example.highpass_backend.dto.admin;

import com.example.highpass_backend.entity.user.User;

import java.time.LocalDateTime;

public record AdminUserResponse(
        String id,
        String email,
        String nickname,
        String name,
        String role,
        String status,
        LocalDateTime joinedAt,
        String region,
        String loginType,
        String socialProvider,
        boolean online,
        LocalDateTime lastSeenAt,
        LocalDateTime deletedAt,
        int posts,
        int comments,
        int reports
) {
    public static AdminUserResponse from(User user, String socialProvider, boolean online, int posts, int comments) {
        User.Status status = user.getStatus() == null ? User.Status.ACTIVE : user.getStatus();
        String region = String.join(" ",
                user.getSiDo() == null ? "" : user.getSiDo(),
                user.getGunGu() == null ? "" : user.getGunGu()
        ).trim();
        String provider = socialProvider == null || socialProvider.isBlank() ? "" : socialProvider;

        return new AdminUserResponse(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getNickname(),
                user.getNickname(),
                user.getRole().name(),
                status.name().toLowerCase(),
                user.getCreatedAt(),
                region,
                provider.isBlank() ? "local" : "social",
                provider,
                online,
                user.getLastSeenAt(),
                user.getDeletedAt(),
                posts,
                comments,
                0
        );
    }
}
