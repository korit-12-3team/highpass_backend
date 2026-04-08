package com.example.highpass_backend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String nickname;
    private String ageRange;
    private String gender;
    private String siDo;
    private String gunGu;
}