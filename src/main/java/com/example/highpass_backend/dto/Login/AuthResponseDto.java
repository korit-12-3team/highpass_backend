package com.example.highpass_backend.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    private String email;
    private String name;
    private String token;
    private String role;

    public static AuthResponseDto of(String email, String name, String token, String role) {
        return new AuthResponseDto(email, name, token, role);
    }
}


