package com.example.highpass_backend.entity.certificate;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_certificate_year_name_round", columnNames = {"exam_year", "certificate_name", "round"})
        }
)
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NationalCertificate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String certificateName;

    @Column(name = "exam_year", nullable = false)
    private int year;

    private int round;

    @Column(name = "written_Apply_Start")
    private LocalDate writtenApplyStart;

    @Column(name = "written_Apply_End")
    private LocalDate writtenApplyEnd;

    @Column(name = "written_Exam_Date")
    private LocalDate writtenExamDate;

    @Column(name = "written_Result_Date")
    private LocalDate writtenResultDate;

//    @Column(name = "qualification_Submit_Date", nullable = true)
//    private LocalDate qualificationSubmitDate;

    @Column(name = "practical_Apply_Start")
    private LocalDate practicalApplyStart;

    @Column(name = "practical_Apply_End")
    private LocalDate practicalApplyEnd;

    @Column(name = "practical_Exam_Date")
    private LocalDate practicalExamDate;

    @Column(name = "practical_Result_Date")
    private LocalDate practicalResultDate;


}
