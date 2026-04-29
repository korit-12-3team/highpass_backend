package com.example.highpass_backend.repository.certificate;

import com.example.highpass_backend.entity.certificate.NationalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NationalCertificateRepository extends JpaRepository<NationalCertificate, Long> {

    Optional<NationalCertificate> findByCertificateNameAndWrittenApplyStartAndPracticalApplyStart(
            String certificateName,
            java.time.LocalDate writtenApplyStart,
            java.time.LocalDate practicalApplyStart
    );

    Optional<NationalCertificate> findByCertificateNameAndYearAndWrittenExamDate(
            String certificateName,
            int year,
            java.time.LocalDate writtenExamDate
    );

    List<NationalCertificate> findAllByOrderByYearAscCertificateNameAscRoundAsc();
}
