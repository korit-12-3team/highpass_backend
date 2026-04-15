package com.example.highpass_backend.dto.chat;

import com.example.highpass_backend.entity.chat.ChatParticipant;
import com.example.highpass_backend.entity.chat.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class ChatRoomResponse {
    private Long id;
    private String name;
    private Long unreadCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private List<ChatMessageDto> messages;

    public ChatRoomResponse(ChatRoom entity, Long currentUserId) {
        this.id = entity.getId();

        ChatParticipant myParticipant = entity.getParticipants().stream()
                .filter(p -> p.getUser() != null && p.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        LocalDateTime myLastReadAt = (myParticipant != null) ? myParticipant.getLastReadAt() : null;

        this.name = entity.getParticipants().stream()
                .filter(p -> p.getUser() != null && !p.getUser().getId().equals(currentUserId))
                .map(p -> p.getUser().getNickname())
                .findFirst()
                .orElse("대화 상대 없음");


        this.messages = new ArrayList<>();
        if (entity.getMessages() != null && !entity.getMessages().isEmpty()) {
            this.messages = entity.getMessages().stream()
                    .map(m -> ChatMessageDto.builder()
                            .roomId(this.id)
                            .senderId(m.getSender() != null ? m.getSender().getId() : 0L)
                            .senderName(m.getSender() != null ? m.getSender().getNickname() : "알 수 없음")
                            .message(m.getMessage())
                            .createdAt(m.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());

            var lastMsgEntity = entity.getMessages().get(entity.getMessages().size() - 1);
            this.lastMessage = lastMsgEntity.getMessage();
            this.lastMessageTime = lastMsgEntity.getCreatedAt();

            this.unreadCount = entity.getMessages().stream()
                    .filter(m -> m.getSender() != null && !m.getSender().getId().equals(currentUserId))
                    .filter(m -> {
                        if (myLastReadAt == null) return true;
                        return m.getCreatedAt().withNano(0).isAfter(myLastReadAt.withNano(0));
                    })
                    .count();
        } else {
            this.unreadCount = 0L;
            this.lastMessage = "대화 내역이 없습니다.";
            this.lastMessageTime = entity.getCreatedAt();
        }
    }
}


//    private int calculateMessageUnreadCount(ChatRoom room, com.example.highpass_backend.entity.chat.ChatMessage msg, Long currentUserId) {
//        if (!msg.getSender().getId().equals(currentUserId)) return 0;
//
//        return (int) room.getParticipants().stream()
//                .filter(p -> !p.getUser().getId().equals(currentUserId)) // 상대방들 중에서
//                .filter(p -> p.getLastReadAt() == null || p.getLastReadAt().withNano(0).isBefore(msg.getCreatedAt().withNano(0)))
//                .count();
//    }
