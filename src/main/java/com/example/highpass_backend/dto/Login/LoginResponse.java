package com.example.highpass_backend.dto.Login;

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