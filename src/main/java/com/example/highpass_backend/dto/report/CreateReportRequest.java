package com.example.highpass_backend.dto.report;

public record CreateReportRequest(
        String targetType,
        String targetId,
        String reasonCode,
        String detail
) {
}
