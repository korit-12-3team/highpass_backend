package com.example.highpass_backend.repository.certificate;

import com.example.highpass_backend.entity.certificate.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByName(String name);
}
