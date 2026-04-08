package com.example.highpass_backend.repository.user;

import com.example.highpass_backend.entity.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
