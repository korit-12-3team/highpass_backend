package com.example.highpass_backend.repository.user;

import com.example.highpass_backend.entity.user.OAuth2Account;
import com.example.highpass_backend.entity.user.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OAuth2AccountRepository extends JpaRepository<OAuth2Account, Long> {

    Optional<OAuth2Account> findByProviderAndProviderId(OAuthProvider provider, String providerId);
    boolean existsByProviderAndProviderId(OAuthProvider provider, String providerId);

}

