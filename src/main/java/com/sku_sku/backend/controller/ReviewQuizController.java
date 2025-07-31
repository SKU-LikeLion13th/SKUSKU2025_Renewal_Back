package com.sku_sku.backend.controller;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.dto.Request.ReviewQuizDTO;
import com.sku_sku.backend.dto.Request.ReviewWeekDTO;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.security.JwtUtility;
import com.sku_sku.backend.service.reviewquiz.ReviewQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewQuizController {
    private final ReviewQuizService reviewQuizService;
    private final JwtUtility jwtUtility;

    //주차별 퀴즈 리스트 조회
    @GetMapping("/reviewWeek/{trackType}")
    public ResponseEntity<List<ReviewWeekDTO.showReviewWeek>> reviewWeekView(@AuthenticationPrincipal Lion lion, @PathVariable TrackType trackType){
        return ResponseEntity.status(HttpStatus.OK).body(reviewQuizService.getReviewWeek(lion, trackType));
    }

    @Operation(summary = "(주희)해당 주차 복습 퀴즈 문제 조회", description = "",
            responses = {@ApiResponse(responseCode = "200", description = "성공")})
    //복습퀴즈 문제들 조회
    @GetMapping("/reviewQuiz/{reviewWeekId}")
    public ResponseEntity<List<ReviewQuizDTO.ShowReviewQuizDetails>> reviewQuizView(@AuthenticationPrincipal Lion lion,@PathVariable Long reviewWeekId){
        return ResponseEntity.status(HttpStatus.OK).body(reviewQuizService.getReviewQuiz(reviewWeekId,lion));
    }

    @Operation(summary = "(주희)복습 퀴즈 풀기", description = "",
            responses = {@ApiResponse(responseCode = "200", description = "성공")})
    //@ApiResponse(responseCode = "409", description = "그 title 이미 있")})
    @PostMapping("/reviewQuiz/solve")
    public ResponseEntity<ReviewQuizDTO.SolveAnswerList> solveReviewQuiz(@AuthenticationPrincipal Lion lion,
                                                                         @RequestBody ReviewQuizDTO.SolveRequest SolveRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewQuizService.solveReviewQuiz(lion,SolveRequest));
    }

}
