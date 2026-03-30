package com.example.highpass_backend.dto.chat;

import lombok.Getter;

@Getter
public class ChatMessageRequest {
    private Long chatRoomId;
    private String message;
}