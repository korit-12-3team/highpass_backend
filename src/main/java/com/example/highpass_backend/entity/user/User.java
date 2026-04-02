package com.example.highpass_backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String ageRange;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(name = "created_at" ,nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 일반 회원 가입 용
    public static User createLocalUser(String email, String encodedPassword, String name) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.name = name;
        return user;
    }

    // 소셜 로그인 용
    public static User createOAuth2User(String email, String name) {
        User user = new User();
        user.email = email;
        user.password = null;
        user.name = name;
        return  user;
    }
}
