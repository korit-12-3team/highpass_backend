package com.example.highpass_backend.dto.study;

import lombok.Getter;

public record StudyCreateRequest (
    String title,
    String content,
    String locationName,
    String address,
    Double latitude,
    Double longitude,
    String placeId
) {}