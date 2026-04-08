package com.example.highpass_backend.service.login.oauth2;

import com.example.highpass_backend.dto.login.OAuth2SignupRequest;
import com.example.highpass_backend.dto.login.OAuth2SignupResult;
import com.example.highpass_backend.entity.user.OAuth2Account;
import com.example.highpass_backend.entity.user.OAuthProvider;
import com.example.highpass_backend.entity.user.User;
import com.example.highpass_backend.repository.user.OAuth2AccountRepository;
import com.example.highpass_backend.repository.user.UserRepository;
import com.example.highpass_backend.security.CookieUtils;
import com.example.highpass_backend.security.JwtProperties;
import com.example.highpass_backend.security.JwtTokenProvider;
import com.example.highpass_backend.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2SignupServiceImpl implements OAuth2SignupService {

    private final UserRepository userRepository;
    private final OAuth2AccountRepository oauth2AccountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final CookieUtils cookieUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    public OAuth2SignupResult signup(OAuth2SignupRequest request, HttpServletResponse response) {
        OAuthProvider provider = OAuthProvider.valueOf(request.getProvider().toUpperCase());

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("소셜 계정 이메일을 받아오지 못했습니다.");
        }

        if (oauth2AccountRepository.existsByProviderAndProviderId(provider, request.getProviderId())) {
            throw new IllegalArgumentException("이미 가입된 소셜 계정입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다. 기존 계정으로 로그인해주세요.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(null)
                .nickname(request.getNickname())
                .ageRange(request.getAgeRange())
                .gender(request.getGender())
                .siDo(request.getSiDo())
                .gunGu(request.getGunGu())
                .build();

        userRepository.save(user);

        OAuth2Account oauth2Account = OAuth2Account.builder()
                .user(user)
                .provider(provider)
                .providerId(request.getProviderId())
                .build();

        oauth2AccountRepository.save(oauth2Account);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        refreshTokenService.save(
                user.getId(),
                refreshToken,
                LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000)
        );

        cookieUtils.addAccessTokenCookie(response, accessToken);
        cookieUtils.addRefreshTokenCookie(response, refreshToken);

        return OAuth2SignupResult.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .redirectUrl("/calendar")
                .build();
    }
}