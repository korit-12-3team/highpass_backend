package com.example.highpass_backend.dto.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;
}