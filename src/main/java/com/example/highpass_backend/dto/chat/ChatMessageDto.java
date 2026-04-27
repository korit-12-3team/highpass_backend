package com.example.highpass_backend.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageDto {

    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String message;
    private LocalDateTime createdAt;
    private Long unreadCount;
    private MessageType type;

    public enum MessageType {
        ENTER, TALK, QUIT, JOIN_REQUEST, APPROVE, NOTICE, READ
    }

    public void setEnterMessage() {
        this.message = this.senderName + "님이 입장하셨습니다.";
    }
}