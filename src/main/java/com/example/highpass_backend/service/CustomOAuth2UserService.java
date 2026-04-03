package com.example.highpass_backend.service;

import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.user.OAuth2UserRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final OAuth2UserRepository oAuth2UserRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String accessToken = userRequest.getAccessToken().getTokenValue();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = (String)attributes.get("sub");
        String email = (String)attributes.get("email");
        String name = (String)attributes.get("name");

        com.example.highpass_backend.entity.user.OAuth2User savedOAuth2User = oAuth2UserRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElse(null);

        User user;

        if(savedOAuth2User == null) {
            user = userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.save(User.createOAuth2User(email, name)));

            oAuth2UserRepository.save(com.example.highpass_backend.entity.user.OAuth2User
                    .create(user, provider, providerId));
        } else {
            user = savedOAuth2User.getUser();
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),   // Role 엔티티가 없으므로 모든 가입자에게 기본권한(USER)부여
                attributes,
                "email"
        );
    }
}