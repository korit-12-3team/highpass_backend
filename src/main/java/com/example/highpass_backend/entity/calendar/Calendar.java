package com.example.highpass_backend.entity.calendar;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Calendar {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDate date;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
}