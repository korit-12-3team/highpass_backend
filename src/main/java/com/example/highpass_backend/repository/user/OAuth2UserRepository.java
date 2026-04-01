package com.example.highpass_backend.repository.user;

import com.example.highpass_backend.entity.user.OAuth2User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OAuth2UserRepository extends JpaRepository<OAuth2User, Long> {

    Optional<OAuth2User> findByProviderAndProviderId(String provider, String providerId);

    boolean existsByUserAndUserId(Long userId, String provider);
}

