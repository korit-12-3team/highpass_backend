package com.example.highpass_backend.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String name;
    private String ageRange;
    private String gender;
    private String region;
}