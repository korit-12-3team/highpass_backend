package com.example.highpass_backend.dto.login;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String redirectUrl;
}