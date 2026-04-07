package com.example.highpass_backend.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2SignupRequest {
    private String email;
    private String nickname;
    private String ageRange;
    private String gender;
    private String siDo;
    private String gunGu;

    private String provider;     // GOOGLE or KAKAO
    private String providerId;
}

