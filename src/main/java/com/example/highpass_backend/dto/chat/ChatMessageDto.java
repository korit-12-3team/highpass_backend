package com.example.highpass_backend.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    private MessageType type;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String message;

    private LocalDateTime createdAt;
    private Long unreadCount;

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    public void setEnterMessage() {
        this.message = this.senderName + "님이 입장하셨습니다.";
    }
}