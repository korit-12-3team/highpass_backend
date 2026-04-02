package com.example.highpass_backend.repository.certificate;

import com.example.highpass_backend.entity.certificate.UserCertificate;
import com.example.highpass_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCertificateRepository extends JpaRepository<UserCertificate, Long> {
    List<UserCertificate> findByUserIdAndNationalCertificate_Id(Long userId, Long nationalCertificateId);
}
