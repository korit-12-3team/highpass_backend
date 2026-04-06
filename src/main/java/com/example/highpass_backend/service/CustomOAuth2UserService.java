package com.example.highpass_backend.service;

import com.example.highpass_backend.entity.user.OAuth2User;
import com.example.highpass_backend.repository.user.OAuth2UserRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final OAuth2UserRepository oAuth2UserRepository;

    @Override
    @Transactional
    public org.springframework.security.oauth2.core.user.OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = (OAuth2User) super.loadUser(userRequest);

        String provider = "kakao";
        String providerId = oAuth2User.getAttribute("id").toString();

        OAuth2UserRepository oauth2UserRepository = null;
        Optional<OAuth2User> existing =
                oauth2UserRepository.findByProviderAndProviderId(provider, providerId);

        // 기존 회원 → 로그인
        return existing.map(auth2User -> new CustomUserDetails(auth2User.getUser(), false)).orElseGet(() -> new CustomUserDetails(provider, providerId, true));

        // 신규 회원 → 추가정보 필요
    }
}