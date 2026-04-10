package com.example.highpass_backend.dto.board;

public record StudyCreateRequest (
    String title,
    String content,
    String locationName,
    String address,
    Double latitude,
    Double longitude,
    String placeId,
    String cert
) {}
