package com.example.highpass_backend.dto.certificate;

import lombok.Getter;

@Getter
public class UserCertificateRequest {

    private Long certificateScheduleId;
    private Boolean isAlarmEnabled;
}