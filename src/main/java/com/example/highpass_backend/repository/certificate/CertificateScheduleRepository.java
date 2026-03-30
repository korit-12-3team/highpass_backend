package com.example.highpass_backend.repository.certificate;

import com.example.highpass_backend.entity.certificate.Certificate;
import com.example.highpass_backend.entity.certificate.CertificateSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateScheduleRepository extends JpaRepository<CertificateSchedule, Long> {

    List<CertificateSchedule> findByCertificate(Certificate certificate);

    List<CertificateSchedule> findByCertificateId(Long certificateId);
}
