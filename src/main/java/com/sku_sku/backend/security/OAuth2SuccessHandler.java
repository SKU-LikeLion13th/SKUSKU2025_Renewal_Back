package com.sku_sku.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.exception.EmptyLionException;
import com.sku_sku.backend.repository.LionRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtility jwtUtility;
    private final LionRepository lionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Lion lion = lionRepository.findByEmail(email).orElseThrow(EmptyLionException::new);

        String jwt = jwtUtility.generateJwt(email, lion.getName(), lion.getTrack(), lion.getRole());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("jwt", jwt);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
