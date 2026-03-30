package com.example.highpass_backend.dto.interaction;

import lombok.Getter;

@Getter
public class CommentRequest {

    private String content;
    private String targetType;
    private Long targetId;
}