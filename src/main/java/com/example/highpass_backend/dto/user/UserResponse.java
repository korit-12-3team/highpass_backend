package com.example.highpass_backend.dto.user;

import com.example.highpass_backend.entity.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String ageRange;
    private String gender;
    private String siDo;
    private String gunGu;
    private String role;
    private String loginType;
    private String socialProvider;
    private boolean online;
    private LocalDateTime lastSeenAt;
    private String avatarVisualClassName;


    public static UserResponse from(User user) {
        return from(user, null);
    }

    public static UserResponse from(User user, String socialProvider) {
        return from(user, socialProvider, false);
    }

    public static UserResponse from(User user, String socialProvider, boolean online) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .ageRange(user.getAgeRange())
                .gender(user.getGender())
                .siDo(user.getSiDo())
                .gunGu(user.getGunGu())
                .role(user.getRole().name())
                .loginType(socialProvider == null ? "local" : "social")
                .socialProvider(socialProvider)
                .online(online)
                .lastSeenAt(user.getLastSeenAt())
                .avatarVisualClassName(user.getAvatarVisualClassName())
                .build();
    }
}
