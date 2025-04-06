package com.sku_sku.backend.controller;

import com.sku_sku.backend.domain.reviewquiz.JoinReviewQuizFile;
import com.sku_sku.backend.repository.JoinReviewQuizFileRepository;
import com.sku_sku.backend.security.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinReviewQuizFileController {

    private final JoinReviewQuizFileRepository fileRepository;
    private final JwtUtility jwtUtility;

    @Operation(summary = "(주희)복습퀴즈 출제", description = "Headers에 Bearer token 필요",
            responses = {@ApiResponse(responseCode = "201", description = "생성")})
    @GetMapping("/api/review-quiz/file/{id}")
    public ResponseEntity<byte[]> getFile(HttpServletRequest request, @PathVariable Long id) {
        String token = jwtUtility.extractTokenFromCookies(request);
        jwtUtility.validateJwt(token);
        JoinReviewQuizFile fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        byte[] fileData = fileEntity.getFile();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG); // 또는 실제 타입 감지해서 동적으로 지정
        headers.setContentLength(fileData.length);

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
}

