package com.example.highpass_backend.controller.certificate;

import com.example.highpass_backend.dto.certificate.UserCertificateRequest;
import com.example.highpass_backend.dto.certificate.UserCertificateResponse;
import com.example.highpass_backend.service.certificate.UserCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-certificates")
@RequiredArgsConstructor
public class UserCertificateController {

    private final UserCertificateService userCertificateService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserCertificateResponse> addUserCertificate(
            @PathVariable Long userId,
            @RequestBody UserCertificateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userCertificateService.addUserCertificate(userId, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserCertificateResponse>> getUserCertificates(@PathVariable Long userId) {
        return ResponseEntity.ok(userCertificateService.getUserCertificates(userId));
    }

    @DeleteMapping("/{userCertificateId}")
    public ResponseEntity<Void> deleteUserCertificate(@PathVariable Long userCertificateId) {
        userCertificateService.deleteUserCertificate(userCertificateId);
        return ResponseEntity.noContent().build();
    }
}
