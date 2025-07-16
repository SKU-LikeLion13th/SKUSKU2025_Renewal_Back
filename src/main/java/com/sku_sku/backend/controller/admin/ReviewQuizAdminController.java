package com.sku_sku.backend.controller.admin;


import com.sku_sku.backend.service.reviewquiz.ReviewQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import static com.sku_sku.backend.dto.Request.ReviewQuizDTO.AddQuizRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ReviewQuizAdminController {
    private final ReviewQuizService reviewQuizService;

    @Operation(summary = "(주희)복습퀴즈 출제", description = "",
            responses = {@ApiResponse(responseCode = "201", description = "생성")})
                    //@ApiResponse(responseCode = "409", description = "그 title 이미 있")})
    @PostMapping("reviewQuiz/add")
    public ResponseEntity<String> makeReviewQuiz(@RequestBody AddQuizRequest req){
        reviewQuizService.addQuiz(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("복습퀴즈 생성 완료");
    }


    @Operation(summary = "(주희)복습퀴즈 수정", description = "",
            responses = {@ApiResponse(responseCode = "200", description = "성공")})
    @PutMapping("reviewQuiz/update/{weekId}")
    public ResponseEntity<String> updateReviewQuiz(@RequestBody AddQuizRequest req, @PathVariable Long weekId) {
        reviewQuizService.updateQuiz(weekId,req);
        return ResponseEntity.status(HttpStatus.OK).body("복습퀴즈 수정 완료");
    }

    @Operation(summary = "(주희)복습퀴즈 삭제", description = "삭제",
            responses = {@ApiResponse(responseCode = "200", description = "성공")})
    @DeleteMapping("reviewQuiz/delete/{weekId}")
    public ResponseEntity<String> updateReviewQuiz(@PathVariable Long weekId) {
        reviewQuizService.deleteQuiz(weekId);
        return ResponseEntity.status(HttpStatus.OK).body("복습퀴즈 삭제 완료");
    }


}
