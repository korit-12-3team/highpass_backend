package com.example.highpass_backend.repository.certificate;

import com.example.highpass_backend.entity.certificate.NationalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NationalCertificateRepository extends JpaRepository<NationalCertificate, Long> {

    List<NationalCertificate> findByCertificate(NationalCertificate nationalCertificate);

    List<NationalCertificate> findByNationalCertificateId(Long nationalCertificateId);
}
