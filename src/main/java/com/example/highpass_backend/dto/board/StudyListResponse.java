package com.example.highpass_backend.dto.board;

import com.example.highpass_backend.entity.board.Study;
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
    private String cert;
    private int viewCount;
    private int likeCount;
    private boolean likedByUser;
    private LocalDateTime createdAt;

    public static StudyListResponse from(Study study) {
        return from(study, false);
    }

    public static StudyListResponse from(Study study, boolean likedByUser) {
        return StudyListResponse.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .userId(study.getUser().getId())
                .nickname(study.getUser().getNickname())
                .locationName(study.getLocationName())
                .cert(study.getCert())
                .viewCount(study.getViewCount())
                .likeCount(study.getLikeCount())
                .likedByUser(likedByUser)
                .createdAt(study.getCreatedAt())
                .build();
    }
}
