package com.example.highpass_backend.dto.study;

import lombok.Getter;

@Getter
public class StudyCreateRequest {

    private String title;
    private String content;

    private String locationName;
    private String address;

    private Double latitude;
    private Double longitude;

    private String placeId;
}