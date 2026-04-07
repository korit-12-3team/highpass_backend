package com.example.highpass_backend.dto.Login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {
    private String email;
    private String password;
    private String nickname;
    private String ageRange;
    private String gender;
    private String siDo;
    private String gunGu;
}
