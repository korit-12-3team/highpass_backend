package com.example.highpass_backend.dto.study;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyListResponse {

    private Long id;
    private String title;
    private String nickname;
    private String locationName;

    private int viewCount;
    private int likeCount;

    private LocalDateTime createdAt;
}