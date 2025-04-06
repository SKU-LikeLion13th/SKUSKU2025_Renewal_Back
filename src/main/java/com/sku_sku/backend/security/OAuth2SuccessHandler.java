package com.sku_sku.backend.security;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.exception.EmptyLionException;
import com.sku_sku.backend.repository.LionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtility jwtUtility;
    private final LionRepository lionRepository;

//    @Value("${custom.frontend-url}")
//    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        System.out.println("✅ [OAuth2SuccessHandler] onAuthenticationSuccess 진입");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Lion lion = lionRepository.findByEmail(email).orElseThrow(EmptyLionException::new);

        String jwt = jwtUtility.generateJwt(email, lion.getName(), lion.getTrackType(), lion.getRoleType());

        // JWT를 HttpOnly Cookie에 저장
        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
//                .secure(true) // 로컬에서는 false
//                .sameSite("None")
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());


        // 프론트엔드로 리디렉트 // 배포하면 사용
        response.sendRedirect("http://localhost:5173/oauth2/redirect");

    }
}
