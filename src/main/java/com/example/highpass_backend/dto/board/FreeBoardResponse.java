package com.example.highpass_backend.dto.board;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FreeBoardResponse {

    private Long id;
    private String title;
    private String content;
    private String nickname;

    private int viewCount;
    private int likeCount;

    private LocalDateTime createdAt;
}