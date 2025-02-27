package com.sku_sku.backend.security;


import com.sku_sku.backend.domain.enums.Role;
import com.sku_sku.backend.domain.enums.Track;
import com.sku_sku.backend.exception.HandleJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtUtility {

    private final SecretKey secretKey; // JWT 서명에 사용되는 비밀 키 // 생성한 비밀 키의 타입이 SecretKey 타입

    private static final long expirationTime = 1000 * 60 * 60; // 밀리초 단위 // JWT 만료 시간: 1시간

    // JWT 서명에 사용되는 비밀 키 생성
    public JwtUtility(@Value("${jwt.base64Secret}") String base64Secret) { // @Value을 통해 application.yml에서 값 주입
        this.secretKey = Keys.hmacShaKeyFor(base64Secret.getBytes());
    }

    // JWT 토큰 생성
    public String generateJwt(String email, String name, Track track, Role role) {
        return Jwts.builder() // JWT 빌더 초기화
                .setSubject(email) // 이메일을 JWT 토큰의 주체로 설정
                .claim("name", name) // JWT 토큰에 이름 추가
                .claim("track", track.name()) // JWT 토큰에 트랙 추가
                .claim("role", role.name()) // JWT 토큰에 역할 추가
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // JWT 토큰의 만료시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS512) // 지정된 알고리즘과 비밀키를 사용하여 JWT 토큰 서명
                .compact(); // JWT 문자열 생성
    }

    // JWT 토큰 유효성 검사
    public boolean validateJwt(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token); // 주어진 JWT 토큰 파싱하여 서명을 검증
            return true; // 올바르면 true 반환
        } catch (ExpiredJwtException e) {
            throw new HandleJwtException("만료된 JWT");
        } catch (UnsupportedJwtException e) {
            throw new HandleJwtException("지원되지 않는 JWT 형식");
        } catch (MalformedJwtException e) {
            throw new HandleJwtException("손상된 JWT");
        } catch (SecurityException e) {
            throw new HandleJwtException("서명이 올바르지 않은 JWT");
        } catch (IllegalArgumentException e) {
            throw new HandleJwtException("JWT가 null이거나 빈 문자열임");
        } catch (JwtException e) {
            throw new HandleJwtException("기타 JWT관련 예외");
        }
    }

    // JWT 토큰에서 클레임을 추출하여 반환
    public Claims getClaimsFromJwt(String jwt) {
        String NoneBearerJwt = jwt;
        // "Bearer "로 시작하면
        if (jwt.startsWith("Bearer ")) {
            NoneBearerJwt = jwt.substring(7); // "Bearer " 부분을 제거
        }
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(NoneBearerJwt)
                .getBody();  // JWT의 페이로드에서 클레임 반환
    }
}
