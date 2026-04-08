package com.example.highpass_backend.service;

import com.example.highpass_backend.dto.login.LoginResponse;
import com.example.highpass_backend.dto.login.UserLoginRequest;
import com.example.highpass_backend.dto.login.UserSignupRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void signup(UserSignupRequest request);
    LoginResponse login(UserLoginRequest request, HttpServletResponse response);
}