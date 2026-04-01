package com.example.highpass_backend.entity.interaction;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(                         // 좋아요 부정 방지
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_user_favorite",
            columnNames = {"user_id", "targetId", "targetType"}
        )
    }
)
public class Favorite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    public enum TargetType {
        STUDY,
        FREE
    }
}