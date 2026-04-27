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
    private String type;
    private Long ownerId;
    private Long unreadCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private List<ChatMessageDto> messages;
    private List<ChatParticipantResponse> participants;

    public ChatRoomResponse(ChatRoom entity, Long currentUserId) {
        this.id = entity.getId();
        this.type = entity.getType().name();
        this.ownerId = entity.getOwnerId();

        this.participants = entity.getParticipants().stream()
                .filter(p -> p.getUser() != null && p.getStatus() != null)
                .map(ChatParticipantResponse::new)
                .collect(Collectors.toList());

        ChatParticipant myParticipation = entity.getParticipants().stream()
                .filter(p -> p.getUser() != null)
                .filter(p -> p.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        LocalDateTime myLastReadAt = (myParticipation != null) ? myParticipation.getLastReadAt() : null;
        LocalDateTime joinedAt = (myParticipation != null) ? myParticipation.getJoinedAt() : null;

        boolean isJoined = myParticipation != null &&
                myParticipation.getStatus() == ChatParticipant.ParticipantStatus.JOINED;

        this.name = entity.getType() == ChatRoom.ChatType.GROUP
                ? entity.getName()
                : this.participants.stream()
                .filter(p -> !p.getUserId().equals(currentUserId))
                .map(ChatParticipantResponse::getNickname)
                .findFirst()
                .orElse("대화 상대 없음");


        this.messages = new ArrayList<>();

        if (!isJoined) {
            this.unreadCount = 0L;
            this.lastMessage = "승인 대기 중입니다.";
            this.lastMessageTime = entity.getCreatedAt();
        } else if (entity.getMessages() != null && !entity.getMessages().isEmpty()) {
            this.messages = entity.getMessages().stream()
                    .filter(m -> joinedAt == null || m.getCreatedAt().isAfter(joinedAt))
                    .map(m -> {
                        long unread = entity.getParticipants().stream()
                                .filter(p -> p.getUser() != null && !p.getUser().getId().equals(currentUserId))
                                .filter(p -> p.getLastReadAt() == null || p.getLastReadAt().isBefore(m.getCreatedAt()))
                                .count();
                        return ChatMessageDto.builder()
                                .roomId(this.id)
                                .senderId(m.getSender() != null ? m.getSender().getId() : 0L)
                                .senderName(m.getSender() != null ? m.getSender().getNickname() : "알 수 없음")
                                .message(m.getMessage())
                                .createdAt(m.getCreatedAt())
                                .type(m.getType() != null ? ChatMessageDto.MessageType.valueOf(m.getType().name()) : ChatMessageDto.MessageType.TALK)
                                .unreadCount(unread)
                                .build();
                    })
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