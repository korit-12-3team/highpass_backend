package com.example.highpass_backend.service;

import com.example.highpass_backend.dto.Login.LoginResponse;
import com.example.highpass_backend.dto.Login.UserLoginRequest;
import com.example.highpass_backend.dto.Login.UserSignupRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void signup(UserSignupRequest request);
    LoginResponse login(UserLoginRequest request, HttpServletResponse response);
}