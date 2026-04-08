package com.example.highpass_backend.controller.certificate;

import com.example.highpass_backend.dto.certificate.CertificateScheduleResponse;
import com.example.highpass_backend.service.certificate.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping(value = "/schedules", produces = "application/json")
    public List<CertificateScheduleResponse> getSchedules() {
        return certificateService.getSchedules();
    }
}