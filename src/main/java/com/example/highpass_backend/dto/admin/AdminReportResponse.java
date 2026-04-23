package com.example.highpass_backend.dto.admin;

public record AdminReportResponse(
        String id,
        String targetType,
        String targetId,
        String targetLabel,
        String reason,
        String reporter,
        String createdAt,
        String status
) {
}
