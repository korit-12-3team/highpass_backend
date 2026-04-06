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
    private static final String FRONTEND_BASE_URL = "https://afraid-duck-0.loca.lt";

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();



        assert userDetails != null;
        if (userDetails.isNew()) {
            response.sendRedirect(
                    FRONTEND_BASE_URL + "/oauth2/signup?provider=" +
                            userDetails.getProvider() +
                            "&providerId=" + userDetails.getProviderId()
            );
        } else {
            response.sendRedirect(FRONTEND_BASE_URL + "/calendar");
        }
    }
}