package com.sku_sku.backend.service;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.exception.InvalidJwtlException;
import com.sku_sku.backend.exception.InvalidLoginlException;
import com.sku_sku.backend.security.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final JwtUtility jwtUtility;
    private final RedisTemplate<String, String> redisTemplate;
    private final LionService lionService;

    @Value("${cookie.secure}")
    private boolean isSecure;

    @Value("${cookie.sameSite}")
    private String isSameSite;

    public Map<String, Object> getLoginStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidLoginlException();
        }

        String email = authentication.getName();
        Lion lion = lionService.findByEmail(email);

        return Map.of(
                "name", lion.getName(),
                "track", lion.getTrackType(),
                "role", lion.getRoleType()
        );
    }

    public void logout(HttpServletResponse response, String email) {
        String redisKey = "refresh:" + email;
        redisTemplate.delete(redisKey);

        ResponseCookie deleteToken = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", deleteToken.toString());
    }

    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtility.extractTokenFromCookies(request);
        if (token == null) throw new InvalidJwtlException("Access");

        String email = jwtUtility.getClaimsFromJwt(token).getSubject();

        String redisKey = "refresh:" + email;
        if (!redisTemplate.hasKey(redisKey)) {
            throw new InvalidJwtlException("Refresh");
        }

        Lion lion = lionService.findByEmail(email);
        String newAccessToken = jwtUtility.generateJwt(email, lion.getName(), lion.getTrackType(), lion.getRoleType());

        ResponseCookie cookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSameSite)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}

