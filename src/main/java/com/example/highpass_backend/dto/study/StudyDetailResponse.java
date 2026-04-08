package com.example.highpass_backend.dto.study;

import com.example.highpass_backend.entity.study.Study;
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

    public static StudyDetailResponse from(Study study) {
        return StudyDetailResponse.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .nickname(study.getUser().getNickname())
                .locationName(study.getLocationName())
                .address(study.getAddress())
                .latitude(study.getLatitude())
                .longitude(study.getLongitude())
                .viewCount(study.getViewCount())
                .likeCount(study.getLikeCount())
                .createdAt(study.getCreatedAt())
                .build();
    }
}