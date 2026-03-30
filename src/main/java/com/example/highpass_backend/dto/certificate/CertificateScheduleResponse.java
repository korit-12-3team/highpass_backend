package com.example.highpass_backend.dto.certificate;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CertificateScheduleResponse {

    private Long id;
    private String certificateName;
    private int round;

    private LocalDate writtenExamDate;
    private LocalDate practicalExamDate;
}
