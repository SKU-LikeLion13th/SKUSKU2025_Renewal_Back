package com.sku_sku.backend.security;

import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.exception.HandleJwtException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter를 상속하여 HTTP 요청마다 한 번만 실행되는 필터임을 나타냄

    private final JwtUtility jwtUtility;

    @Override // 각 HTTP 요청마다 실행
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request); // 헤더에서 JWT 토큰 추출
            if (token != null && jwtUtility.validateJwt(token)) { // 유효한 JWT 토큰 반환시
                Authentication auth = getAuthentication(token); // 인증 객체 생성
                SecurityContextHolder.getContext().setAuthentication(auth); // 현재 실행 중인 스레드의 보안 컴텍스트에 인증 정보를 설정하여 이후 요청이 인증된 사용자로 인식되도록 함
            }
            filterChain.doFilter(request, response); // 다음 필터로 요청 전달
        } catch (HandleJwtException e) { // validateJwt 메서드에서 발생한 예외
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized 반환
            response.getWriter().write(e.getMessage());
        }
    }

    // 헤더에서 JWT 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization"); // Authorization 헤더에서 토큰 추출
        // Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 null 반환
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7); // "Bearer " 부분을 제거하고 JWT만 반환
    }

    // JWT 토큰에서 사용자 인증 정보 생성
    private Authentication getAuthentication(String jwt) {
        Claims claims = jwtUtility.getClaimsFromJwt(jwt);
        String email = claims.getSubject();
        RoleType roleType = RoleType.valueOf(claims.get("role", String.class));

        return new UsernamePasswordAuthenticationToken(email,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleType.name())));
    }
}
