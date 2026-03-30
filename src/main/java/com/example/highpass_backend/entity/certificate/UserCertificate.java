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
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private CertificateSchedule certificateSchedule;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean isAlarmEnabled;

    private LocalDateTime createdAt;

    public enum Status {
        PREPARING,
        PASSED,
        FAILED
    }
}