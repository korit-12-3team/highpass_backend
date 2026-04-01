package com.example.highpass_backend.entity.study;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Study {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = true, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String locationName;

    @Column(nullable = false)
    private String placeId;

    @Column(nullable = false, length = 50)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Builder.Default
    @Column(nullable = false)
    private int viewCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private int favoriteCount = 0;

    @CreatedDate
    @Column(name = "created_at" ,nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void incrementViewCount() {
        this.viewCount++;
    }
}