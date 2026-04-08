package com.example.highpass_backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;          // 소셜도 저장, nullable 가능 여부는 정책에 따라

    private String password;       // 일반 회원만 저장, 소셜은 null

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "age_range", length = 20)
    private String ageRange;

    @Column(length = 20)
    private String gender;

    @Column(length = 50)
    private String siDo;

    @Column(length = 50)
    private String gunGu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }

//    @PreUpdate
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfile(String nickname, String ageRange, String gender, String siDo, String gunGu) {
        this.nickname = nickname;
        this.ageRange = ageRange;
        this.gender = gender;
        this.siDo = siDo;
        this.gunGu = gunGu;
    }
}
