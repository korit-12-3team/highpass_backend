package com.example.highpass_backend.entity.calendar;

import com.example.highpass_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String content;

    public void updateCalendar(LocalDate date, String title, String content) {
        this.date = date;
        this.title = title;
        this.content = content;
    }
}