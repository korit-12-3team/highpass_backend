package com.example.highpass_backend.controller;

import com.example.highpass_backend.dto.Login.AuthResponseDto;
import com.example.highpass_backend.dto.Login.LoginRequestDto;
import com.example.highpass_backend.dto.Login.SignupRequestDto;
import com.example.highpass_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@Valid @RequestBody SignupRequestDto dto) {
        return ResponseEntity.ok(userService.signup(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @GetMapping("/oauth2/google/url")
    public ResponseEntity<String> getGoogleLoginUrl() {
        return ResponseEntity.ok("http://localhost:8080/oauth2/authorization/google");
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.badRequest().body("소셜 로그인에 실패했습니다.");
    }
}
