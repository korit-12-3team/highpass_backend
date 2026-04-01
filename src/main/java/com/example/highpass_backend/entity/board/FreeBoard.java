package com.example.highpass_backend.entity.board;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class FreeBoard {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int favoriteCount;

    @Column(name = "created_at" ,nullable = false)
    private LocalDateTime createdAt;
}