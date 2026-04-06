package com.example.highpass_backend.eception;

import com.example.highpass_backend.service.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        assert userDetails != null;
        if (userDetails.isNew()) {
            // 추가 회원가입 필요
            response.sendRedirect(
                    "http://localhost:3000/oauth2/signup?provider=" +
                            userDetails.getProvider() +
                            "&providerId=" + userDetails.getProviderId()
            );
        } else {
            // 로그인 완료
            response.sendRedirect("https://free-waves-drive.loca.lt/calendar");
        }
    }
}