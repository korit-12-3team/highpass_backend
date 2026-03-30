package com.example.highpass_backend.entity.interaction;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
public class Favorite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long targetId;

    public enum TargetType {
        STUDY,
        FREE
    }
}