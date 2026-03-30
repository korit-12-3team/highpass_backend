package com.example.highpass_backend.entity.interaction;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String content;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long targetId;

    private LocalDateTime createdAt;

    public enum TargetType {
        STUDY,
        FREE
    }
}