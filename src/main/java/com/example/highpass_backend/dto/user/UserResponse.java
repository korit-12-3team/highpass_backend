package com.example.highpass_backend.dto.user;

import com.example.highpass_backend.entity.user.User;
import lombok.Builder;
import lombok.Getter;

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
    private String loginType;
    private String socialProvider;


    public static UserResponse from(User user) {
        return from(user, null);
    }

    public static UserResponse from(User user, String socialProvider) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .ageRange(user.getAgeRange())
                .gender(user.getGender())
                .siDo(user.getSiDo())
                .gunGu(user.getGunGu())
                .loginType(socialProvider == null ? "local" : "social")
                .socialProvider(socialProvider)
                .build();
    }
}
