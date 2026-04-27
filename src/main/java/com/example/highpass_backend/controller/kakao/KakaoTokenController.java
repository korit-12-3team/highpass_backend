package com.example.highpass_backend.controller.kakao;

import com.example.highpass_backend.service.kakao.KakaoCalendarTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kakao")
public class KakaoTokenController {

    private final KakaoCalendarTokenService kakaoCalendarTokenService;

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getKakaoToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String accessToken = kakaoCalendarTokenService.getAccessToken(request, response);
        return ResponseEntity.ok(Map.of("kakaoAccessToken", accessToken));
    }
}
