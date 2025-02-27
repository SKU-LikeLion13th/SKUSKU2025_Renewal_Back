package com.sku_sku.backend.controller;

import com.sku_sku.backend.service.CustomOAuth2UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "구글 로그인 세션 관련")
public class OAuth2Controller {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Operation(summary = "(민규) 구글 로그인", description = "body에 token 필요",
            responses = {@ApiResponse(responseCode = "200", description = "bearer token 발급")})
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody OAuth2UserRequest requestPayload) {
        return customOAuth2UserService.loadUser(requestPayload);
    }
}
