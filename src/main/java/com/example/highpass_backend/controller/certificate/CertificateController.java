package com.example.highpass_backend.controller.certificate;

import com.example.highpass_backend.dto.certificate.CertificateScheduleResponse;
import com.example.highpass_backend.dto.certificate.CertificateSyncResponse;
import com.example.highpass_backend.service.certificate.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(value = "/admin/sync", produces = "application/json")
    public ResponseEntity<CertificateSyncResponse> syncSchedules() {
        // TODO: 관리자 계정/권한 체계가 추가되면 이 엔드포인트는 관리자만 호출 가능하도록 제한해야 합니다.
        return ResponseEntity.ok(certificateService.syncSchedules());
    }
}
