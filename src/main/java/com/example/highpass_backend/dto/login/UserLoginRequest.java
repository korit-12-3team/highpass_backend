package com.example.highpass_backend.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    private String email;
    private String password;
}