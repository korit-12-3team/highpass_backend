package com.example.highpass_backend.dto.interaction;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private String nickname;

    private LocalDateTime createdAt;
}