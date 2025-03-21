//package com.sku_sku.backend.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
////@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final OAuth2SuccessHandler oAuth2SuccessHandler;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.httpBasic(AbstractHttpConfigurer::disable) // Spring Security의 기본 인증 방식인 Basic Authentication을 비활성화
//                .csrf(AbstractHttpConfigurer::disable) // JWT는 CSRF 공격에 취약하지 않아 CSRF 보호 비활성화 // 보통 CSRF 보호는 세션 기반 인증을 위해 사용
//                .formLogin(AbstractHttpConfigurer::disable) //  Spring Security의 기본 폼 로그인 기능을 비활성화
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT를 사용하기 때문에 세션을 사용하지 않도록 STATELESS로 설정
//                .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2SuccessHandler))
//                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI와 API 문서화 경로에 대한 접근을 모든 사용자에게 허용
//                                .requestMatchers("/api/auth/**").permitAll() // 인증 관련 API 경로에 대한 접근을 모든 사용자에게 허용.anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
//                                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 각 요청에 대해 JWT 토큰을 검증하고, 유효한 토큰이면 인증 정보를 설정
//
//        return http.build(); // 설정이 완료된 HttpSecurity 객체를 빌드하여 SecurityFilterChain을 반환
//    }
//}