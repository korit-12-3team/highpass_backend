package com.example.highpass_backend.entity.certificate;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class UserCertificate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserCertificate usercertificate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean isAlarmEnabled;


    public enum Status {
        PREPARING,
        PASSED,
        FAILED
    }
}