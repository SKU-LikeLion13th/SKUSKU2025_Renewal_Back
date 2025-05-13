package com.sku_sku.backend.controller;

import com.sku_sku.backend.dto.Request.ReviewQuizDTO;
import com.sku_sku.backend.dto.Request.ReviewWeekDTO;
import com.sku_sku.backend.security.JwtUtility;
import com.sku_sku.backend.service.ReviewQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewQuizController {
    private final ReviewQuizService reviewQuizService;
    private final JwtUtility jwtUtility;

    //주차별 퀴즈 리스트 조회

    @GetMapping("/reviewWeek")
    public List<ReviewWeekDTO.showReviewWeek> reviewWeekView(@RequestHeader("Authorization") String bearer){
        return null;
    }

    //복습퀴즈 문제들 조회
    @GetMapping("/reviewQuiz/{reviewWeekId}")
    public ReviewQuizDTO.ShowReviewQuizDetails reviewQuizView(@PathVariable Long reviewWeekId){
        return null;
    }

    @Operation(summary = "(주희)복습 퀴즈 풀기", description = "",
            responses = {@ApiResponse(responseCode = "200", description = "성공")})
    //@ApiResponse(responseCode = "409", description = "그 title 이미 있")})
    @PostMapping("/reviewQuiz/solve")
    public ResponseEntity<ReviewQuizDTO.SolveAnswerList> solveReviewQuiz(HttpServletRequest request,
                                                                         @RequestBody ReviewQuizDTO.SolveRequest SolveRequest) {
        String token = jwtUtility.extractTokenFromCookies(request);
        return ResponseEntity.status(HttpStatus.OK).body(reviewQuizService.solveReviewQuiz(token,SolveRequest));


    }

//    public String extractTokenFromCookies(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("token".equals(cookie.getName())) { // 쿠키 이름이 'token'일 경우
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null; // 토큰이 없으면 null 반환
//    }
}
