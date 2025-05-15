package com.sku_sku.backend.controller.admin;


import com.sku_sku.backend.dto.Request.ReviewQuizDTO;
import com.sku_sku.backend.service.ReviewQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.sku_sku.backend.dto.Request.ReviewQuizDTO.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ReviewQuizAdminController {
    private final ReviewQuizService reviewQuizService;

    @Operation(summary = "(주희)복습퀴즈 출제", description = "",
            responses = {@ApiResponse(responseCode = "201", description = "생성")})
                    //@ApiResponse(responseCode = "409", description = "그 title 이미 있")})
    @PostMapping("reviewQuiz/add")
    public ResponseEntity<String> makeReviewQuiz(@RequestBody AddQuizRequest req) throws IOException {
        reviewQuizService.addQuiz(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("복습퀴즈 생성 완료");
    }


}
