package com.example.highpass_backend.dto.report;

public record CreateSupportInquiryRequest(
        String email,
        String title,
        String reasonCode,
        String detail
) {
}
