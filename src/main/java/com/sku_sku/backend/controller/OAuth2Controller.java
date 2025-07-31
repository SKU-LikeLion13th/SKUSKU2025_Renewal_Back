package com.sku_sku.backend.controller;

import com.sku_sku.backend.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/log")
@Tag(name = "로그인 관련 기능")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @Operation(summary = "(민규) 로그인 상태", description = "로그인 한 상태면 로그인한 유저의 정보를, 로그인이 안 된 상태면 에러 반환",
            responses = {@ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404")})
    @GetMapping("/status")
    public ResponseEntity<?> loginStatus(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(oAuth2Service.getLoginStatus(authentication));
    }

    @Operation(summary = "(민규) 로그아웃", description = "",
            responses = {@ApiResponse(responseCode = "200", description = "로그아웃 성공")})
    @PostMapping("/out")
    public ResponseEntity<String> logout(HttpServletResponse response, Authentication auth) {
        oAuth2Service.logout(response, auth.getName());
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
    }

    @Operation(summary = "(민규) access_token 재발급 테스트용",
            responses = {@ApiResponse(responseCode = "200", description = "Access token 재발급 완료"),
                    @ApiResponse(responseCode = "404", description = "그 id에 해당하는 값 없")})
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        oAuth2Service.refreshAccessToken(request, response);
        return ResponseEntity.status(HttpStatus.OK).body("Access token 재발급 완료");
    }

}
