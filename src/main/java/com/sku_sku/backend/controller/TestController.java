package com.sku_sku.backend.controller;

import com.sku_sku.backend.security.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "api test 관련")
public class TestController {

    private final JwtUtility jwtUtility;

    @Operation(summary = "(민규) test입니다 신경 쓰지 마세요")
    @GetMapping("test")
    public String test(HttpServletRequest header) {
        String token = jwtUtility.extractTokenFromCookies(header);
        return jwtUtility.getClaimsFromJwt(token).getSubject();
    }
}
