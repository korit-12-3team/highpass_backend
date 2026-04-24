package com.example.highpass_backend.controller.report;

import com.example.highpass_backend.dto.report.CreateReportRequest;
import com.example.highpass_backend.dto.report.ReportResponse;
import com.example.highpass_backend.security.CustomJwtPrincipal;
import com.example.highpass_backend.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal CustomJwtPrincipal principal
    ) {
        ReportResponse response = reportService.createReport(principal.getUserId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
