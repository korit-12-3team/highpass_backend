package com.example.highpass_backend.dto.board;

import com.example.highpass_backend.entity.board.Comment;
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

    public static CommentResponse from (Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}