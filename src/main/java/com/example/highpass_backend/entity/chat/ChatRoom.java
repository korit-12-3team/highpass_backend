package com.example.highpass_backend.entity.chat;

import com.example.highpass_backend.entity.board.StudyBoard;
import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<ChatMessage> messages = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

    public enum ChatType {
        PERSONAL, GROUP
    }

    @Enumerated(EnumType.STRING)
    private ChatType type;

    private Long ownerId;

    private boolean isApprovalRequired;

    @Builder
    public ChatRoom(String name, ChatType type, boolean isApprovalRequired, Long ownerId) {
        this.name = name;
        this.type = type;
        this.isApprovalRequired = isApprovalRequired;
        this.ownerId = ownerId;
        this.participants = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void addParticipant(User user, boolean isOwner) {
        ChatParticipant.ParticipantStatus initialStatus;

        if (this.type == ChatType.PERSONAL || !this.isApprovalRequired || isOwner ) {
            initialStatus = ChatParticipant.ParticipantStatus.JOINED;
        } else {
            initialStatus = ChatParticipant.ParticipantStatus.PENDING;
        }

        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(this)
                .user(user)
                .status(initialStatus)
                .isOwner(isOwner)
                .build();

        this.participants.add(participant);

        if(isOwner) {
            this.ownerId = user.getId();
        }
    }
}