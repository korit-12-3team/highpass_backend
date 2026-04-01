package com.example.highpass_backend.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    private String email;
    private String name;

    public static AuthResponseDto of(String email, String name) {
        return new AuthResponseDto(email, name);
    }
}


