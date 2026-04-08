package com.example.highpass_backend.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2SignupResult {
    private Long userId;
    private String email;
    private String nickname;
    private String redirectUrl;
}