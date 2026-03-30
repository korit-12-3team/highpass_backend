package com.example.highpass_backend.entity.certificate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class CertificateSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Certificate certificate;

    private int round;

    private LocalDate writtenApplyStart;
    private LocalDate writtenApplyEnd;
    private LocalDate writtenExamDate;
    private LocalDate writtenResultDate;

    private LocalDate practicalApplyStart;
    private LocalDate practicalApplyEnd;
    private LocalDate practicalExamDate;
    private LocalDate practicalResultDate;
}