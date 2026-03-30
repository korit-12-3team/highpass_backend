package com.example.highpass_backend.entity.study;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Study {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String locationName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String placeId;

    private int viewCount;
    private int favoriteCount;

    private LocalDateTime createdAt;
}