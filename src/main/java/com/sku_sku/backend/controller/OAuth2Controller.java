package com.sku_sku.backend.controller;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.security.JwtUtility;
import com.sku_sku.backend.service.LionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/log")
@Tag(name = "로그인 관련 기능")
public class OAuth2Controller {

    private final LionService lionService;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtility jwtUtility;

    @Operation(summary = "(민규) 로그인 상태 조회", description = "로그인한 유저가 있다면 그 유저의 정보를 반환, 로그인이 안 되어 있으면 401 반환",
            responses = {@ApiResponse(responseCode = "200", description = "로그인한 유저 정보 반환"),
                    @ApiResponse(responseCode = "401", description = "로그인이 안 되어 있음")})
    @GetMapping("/status")
    public ResponseEntity<?> loginStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 안 되어 있음");
        }

        String email = authentication.getName();
        Lion lion = lionService.findByEmail(email);

        Map<String, Object> userInfo = Map.of(
                "name", lion.getName(),
                "track", lion.getTrackType(),
                "role", lion.getRoleType()
        );

        return ResponseEntity.status(HttpStatus.OK).body(userInfo);
    }

    @Operation(summary = "(민규) 로그아웃", description = "로그아웃",
            responses = @ApiResponse(responseCode = "200", description = "로그아웃 성공"))
    @PostMapping("/out")
    public ResponseEntity<?> logout(HttpServletResponse response, Authentication auth) {
        String email = auth.getName();
        String redisKey = "refresh:" + email;

        if (redisTemplate.hasKey(redisKey)) {
            redisTemplate.delete(redisKey);
        }

        ResponseCookie deleteToken = ResponseCookie.from("access_token", "")
                .httpOnly(true)
//                .secure(true) // 로컬에서는 false
//                .sameSite("None")
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", deleteToken.toString());

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "(민규) Access token 재발급", description = "액세스 토큰이 만료되었을 때 리프레시 토큰을 이용하여 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtility.extractTokenFromCookies(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token 없음");
        }

        try {
            // 유효하지 않지만 claims 추출은 가능한 경우를 대비한 try-catch
            String email = jwtUtility.getClaimsFromJwt(token).getSubject();

            // Redis에 refresh token 존재 확인
            String redisKey = "refresh:" + email;
            if (redisTemplate.hasKey(redisKey)) {
                Lion lion = lionService.findByEmail(email);
                String newAccessToken = jwtUtility.generateJwt(email, lion.getName(), lion.getTrackType(), lion.getRoleType());

                ResponseCookie cookie = ResponseCookie.from("access_token", newAccessToken)
                        .httpOnly(true)
//                        .secure(true) // 로컬에서는 false
//                        .sameSite("None")
                        .secure(false)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(Duration.ofHours(1))
                        .build();

                response.addHeader("Set-Cookie", cookie.toString());

                return ResponseEntity.ok("Access token 재발급 완료");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token 없음. 다시 로그인 필요");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰");
        }
    }

}
