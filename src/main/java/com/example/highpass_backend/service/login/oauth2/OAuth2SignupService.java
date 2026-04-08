package com.example.highpass_backend.service.login.oauth2;

import com.example.highpass_backend.dto.login.OAuth2SignupRequest;
import com.example.highpass_backend.dto.login.OAuth2SignupResult;
import jakarta.servlet.http.HttpServletResponse;

public interface OAuth2SignupService {
    OAuth2SignupResult signup(OAuth2SignupRequest request, HttpServletResponse response);
}