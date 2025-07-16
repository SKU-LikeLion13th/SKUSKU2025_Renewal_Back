package com.sku_sku.backend.security;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.exception.HandleJwtException;
import com.sku_sku.backend.service.LionService;
import com.sku_sku.backend.service.OAuth2Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;
    private final OAuth2Service oAuth2Service; // 주입 추가
    private final RedisTemplate<String, String> redisTemplate; // 주입 추가
    private final LionService lionService; // SecurityContext 재설정 위해 필요

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtility.extractTokenFromCookies(request);

        if (token != null) {
            try {
                if (jwtUtility.validateJwt(token)) {
                    Authentication auth = getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (ExpiredJwtException e) {
                Claims claims = e.getClaims();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                if ("ADMIN_LION".equals(role)) {
                    String redisKey = "refresh:" + email;
                    String refreshToken = redisTemplate.opsForValue().get(redisKey);

                    if (refreshToken != null) {
                        // Access 토큰 재발급 및 쿠키 세팅
                        String newJwt = oAuth2Service.refreshAccessTokenInJwtAuthenticationFilter(email, response);

                        // 새 토큰으로 인증 정보 셋팅
//                        Lion lion = lionService.findByEmail(email);
//                        String newJwt = jwtUtility.generateJwt(
//                                lion.getEmail(), lion.getName(), lion.getTrackType(), lion.getRoleType()
//                        );
                        Authentication auth = getAuthentication(newJwt);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                // else: 다른 유저는 그냥 인증 없이 통과 → controller에서 401
            } catch (HandleJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String jwt) {
        Claims claims = jwtUtility.getClaimsFromJwt(jwt);
        Lion lion = lionService.findByEmail(claims.getSubject());
        RoleType roleType = RoleType.valueOf(claims.get("role", String.class));

        return new UsernamePasswordAuthenticationToken(
                lion,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleType.name()))
        );
    }
}
