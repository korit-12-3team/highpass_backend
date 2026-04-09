package com.example.highpass_backend.repository.certificate;

import com.example.highpass_backend.entity.certificate.NationalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NationalCertificateRepository extends JpaRepository<NationalCertificate, Long> {
    boolean existsByYearAndCertificateNameAndRound(int year, String certificateName, int round);

}
