package com.example.highpass_backend.security;

import com.example.highpass_backend.config.AppProperties;
import com.example.highpass_backend.service.OAuth2UserPrincipal;
import com.example.highpass_backend.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AppProperties appProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtils cookieUtils;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        String frontendUrl = appProperties.getFrontendUrl();

        if (principal.isNew()) {
            String redirectUrl = frontendUrl + "/signup/"
                    + "?email=" + URLEncoder.encode(
                    principal.getEmail() == null ? "" : principal.getEmail(),
                    StandardCharsets.UTF_8
            )
                    + "&provider=" + principal.getProvider().name()
                    + "&providerId=" + URLEncoder.encode(
                    principal.getProviderId(),
                    StandardCharsets.UTF_8
            )
                    + "&nickname=" + URLEncoder.encode(
                    principal.getNickname() == null ? "" : principal.getNickname(),
                    StandardCharsets.UTF_8
            );

            response.sendRedirect(redirectUrl);
            return;
        }

        String accessToken = jwtTokenProvider.createAccessToken(principal.getUserId(), principal.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(principal.getUserId());

        refreshTokenService.save(
                principal.getUserId(),
                refreshToken,
                LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000)
        );

        cookieUtils.addAccessTokenCookie(response, accessToken);
        cookieUtils.addRefreshTokenCookie(response, refreshToken);

        response.sendRedirect(frontendUrl + "/calendar");
    }
}