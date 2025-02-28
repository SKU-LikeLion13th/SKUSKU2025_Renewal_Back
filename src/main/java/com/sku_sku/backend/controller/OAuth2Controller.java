package com.sku_sku.backend.controller;

import com.sku_sku.backend.domain.enums.Role;
import com.sku_sku.backend.domain.enums.Track;
import com.sku_sku.backend.security.JwtUtility;
import com.sku_sku.backend.service.CustomOAuth2UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "구글 로그인 세션 관련")
public class OAuth2Controller {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtility jwtUtility;

    @Operation(summary = "(민규) 구글 로그인", description = "",
            responses = {@ApiResponse(responseCode = "200", description = "jwt 발급")})
    @GetMapping("/oauth2-success")
    public void oauth2Sucess(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        Track track = Track.valueOf((String) attributes.get("track"));
        Role role = Role.valueOf((String) attributes.get("role"));
        // JWT 생성
        String jwt = jwtUtility.generateJwt(email, name, track, role);
//        String jwt = jwtUtility.generateJwt(
//                (String) attributes.get("sub"),
//                (String) attributes.get("name"),
//                Track.valueOf((String) attributes.get("track")),
//                Role.valueOf((String) attributes.get("role")));

        // JWT를 클라이언트에 반환 (리다이렉트 방식)
        response.sendRedirect("https://sku-sku.com/oauth2/callback?token=" + jwt);
    }
}
