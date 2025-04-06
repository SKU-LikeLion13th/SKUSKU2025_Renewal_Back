package com.sku_sku.backend.controller;

import com.sku_sku.backend.dto.Request.ReviewQuizDTO;
import com.sku_sku.backend.dto.Request.ReviewWeekDTO;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.security.JwtUtility;
import com.sku_sku.backend.service.ReviewQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewQuizController {
    private final ReviewQuizService reviewQuizService;
    private final JwtUtility jwtUtility;

    //주차별 퀴즈 리스트 조회
    @Operation(summary = "(주희)주차별 퀴즈 리스트 조회", description = "Headers에 Bearer token 필요",
            responses = {@ApiResponse(responseCode = "200", description = "미제출일 경우 score, total 둘다 0.  total은 객관식 총 개수 입니다~")})
    @GetMapping("/reviewWeek")
    public List<ReviewWeekDTO.showReviewWeek> reviewWeekView(HttpServletRequest request){
        String token = jwtUtility.extractTokenFromCookies(request);
        return reviewQuizService.getReviewWeek(token);
    }

    //복습퀴즈 문제들 조회
    @GetMapping("/reviewQuiz/{reviewWeekId}")
    public List<ReviewQuizDTO.ShowReviewQuizDetails> reviewQuizView(HttpServletRequest request,@PathVariable Long reviewWeekId){
        String token = jwtUtility.extractTokenFromCookies(request);
        return reviewQuizService.getReviewQuiz(token,reviewWeekId);
    }


    //복습 퀴즈 풀기
    @Operation(summary = "(주희)복습퀴즈 풀기", description = "Headers에 Bearer token 필요",
            responses = {@ApiResponse(responseCode = "200", description = "풀기 완료")})
    @PostMapping("/reviewQuiz/solve")
    public ReviewQuizDTO.SolveAnswerList solveReviewQuiz(HttpServletRequest request,
                                                         @RequestBody ReviewQuizDTO.SolveRequest SolveRequest) {
        String token = jwtUtility.extractTokenFromCookies(request);
        return reviewQuizService.solveReviewQuiz(token,SolveRequest);
    }

}
