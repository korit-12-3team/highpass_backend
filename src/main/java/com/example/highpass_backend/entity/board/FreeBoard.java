package com.example.highpass_backend.entity.board;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)  // 생성일 자동화
public class FreeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    @Column(nullable = false)
    private int viewCount = 0;

    @Builder.Default    // 생성 시각 자동 주입
    @Column(nullable = false)
    private int favoriteCount = 0;

    @CreatedDate
    @Column(name = "created_at" ,nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }
}