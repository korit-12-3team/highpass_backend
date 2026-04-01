package com.example.highpass_backend.entity.certificate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter

public class NationalCertificate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int round;

    @Column(name = "writtenApplyStart" ,nullable = false)
    private LocalDate writtenApplyStart;

    @Column(name = "writtenApplyEnd" ,nullable = false)
    private LocalDate writtenApplyEnd;

    @Column(name = "writtenExamDate" ,nullable = false)
    private LocalDate writtenExamDate;

    @Column(name = "writtenResultDate" ,nullable = false)
    private LocalDate writtenResultDate;

    @Column(name = "qualificationSubmitDate" ,nullable = true)
    private LocalDate qualificationSubmitDate;

    @Column(name = "practicalApplyStart" ,nullable = false)
    private LocalDate practicalApplyStart;

    @Column(name = "practicalApplyEnd" ,nullable = false)
    private LocalDate practicalApplyEnd;

    @Column(name = "practicalExamDate" ,nullable = false)
    private LocalDate practicalExamDate;

    @Column(name = "practicalResultDate" ,nullable = false)
    private LocalDate practicalResultDate;


}
