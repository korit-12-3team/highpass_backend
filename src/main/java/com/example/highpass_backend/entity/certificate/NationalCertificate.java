package com.example.highpass_backend.entity.certificate;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NationalCertificate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int round;

    @Column(name = "written_Apply_Start", nullable = false)
    private LocalDate writtenApplyStart;

    @Column(name = "written_Apply_End", nullable = false)
    private LocalDate writtenApplyEnd;

    @Column(name = "written_Exam_Date", nullable = false)
    private LocalDate writtenExamDate;

    @Column(name = "written_Result_Date", nullable = false)
    private LocalDate writtenResultDate;

    @Column(name = "qualification_Submit_Date", nullable = true)
    private LocalDate qualificationSubmitDate;

    @Column(name = "practical_Apply_Start", nullable = false)
    private LocalDate practicalApplyStart;

    @Column(name = "practical_Apply_End", nullable = false)
    private LocalDate practicalApplyEnd;

    @Column(name = "practical_Exam_Date", nullable = false)
    private LocalDate practicalExamDate;

    @Column(name = "practical_Result_Date", nullable = false)
    private LocalDate practicalResultDate;

    public void updateSchedule(int round, Date writtenApplyStart, Date writtenApplyEnd, Date writtenExamDate, Date writtenResultDate,
                                Date qualificationSubmitDate, Date practicalApplyStart, Date practicalApplyEnd, Date practicalExamDate,
                               Date practicalResultDate) {
        this.round = round;
    }

}
