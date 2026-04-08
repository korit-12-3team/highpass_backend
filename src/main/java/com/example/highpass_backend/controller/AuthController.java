package com.example.highpass_backend.controller;

import com.example.highpass_backend.dto.ApiResponse;
import com.example.highpass_backend.dto.login.LoginResponse;
import com.example.highpass_backend.dto.login.UserLoginRequest;
import com.example.highpass_backend.dto.login.UserSignupRequest;
import com.example.highpass_backend.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody UserSignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(new ApiResponse("회원가입 완료"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody UserLoginRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(request, response));
    }
}