package com.example.highpass_backend.dto.study;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyDetailResponse {

    private Long id;
    private String title;
    private String content;

    private String nickname;

    private String locationName;
    private String address;

    private Double latitude;
    private Double longitude;

    private int viewCount;
    private int likeCount;

    private LocalDateTime createdAt;
}