package com.example.highpass_backend.dto.board;

import com.example.highpass_backend.entity.board.StudyBoard;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyBoardDetailResponse {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String nickname;
    private String locationName;
    private String cert;
    private String address;
    private Double latitude;
    private Double longitude;
    private int viewCount;
    private int likeCount;
    private boolean likedByUser;
    private LocalDateTime createdAt;

    public static StudyBoardDetailResponse from(StudyBoard study) {
        return from(study, false);
    }

    public static StudyBoardDetailResponse from(StudyBoard study, boolean likedByUser) {
        return StudyBoardDetailResponse.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .userId(study.getUser().getId())
                .nickname(study.getUser().getNickname())
                .locationName(study.getLocationName())
                .cert(study.getCert())
                .address(study.getAddress())
                .latitude(study.getLatitude())
                .longitude(study.getLongitude())
                .viewCount(study.getViewCount())
                .likeCount(study.getLikeCount())
                .likedByUser(likedByUser)
                .createdAt(study.getCreatedAt())
                .build();
    }
}
