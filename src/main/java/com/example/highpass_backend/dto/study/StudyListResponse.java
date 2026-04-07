package com.example.highpass_backend.dto.study;

import com.example.highpass_backend.entity.study.Study;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyListResponse {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String nickname;
    private String locationName;
    private int viewCount;
    private int likeCount;
    private LocalDateTime createdAt;

    public static StudyListResponse from(Study study) {
        return StudyListResponse.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .userId(study.getUser().getId())
                .nickname(study.getUser().getNickname())
                .locationName(study.getLocationName())
                .viewCount(study.getViewCount())
                .likeCount(study.getLikeCount())
                .createdAt(study.getCreatedAt())
                .build();
    }
}