package com.sku_sku.backend.controller.admin;

import com.sku_sku.backend.dto.Request.AssignmentDTO.*;
import com.sku_sku.backend.dto.Response.AssignmentDTO.*;
import com.sku_sku.backend.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/assignment")
@Tag(name="관리자 기능: 과제 관련")
public class AssignmentAdminController {

    private final AssignmentService assignmentService;


    @Operation(summary = "(오현) 운영진 과제 업로드", description = "제목, 설명, 과제 타입, 트랙 타입을 받음",
            responses = {@ApiResponse(responseCode = "201", description = "과제 업로드 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 오류"),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자일 경우"),
                    @ApiResponse(responseCode = "400", description = "필수 값이 누락되었을 경우")})
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadAssignment(HttpServletRequest header, @RequestBody UploadAssignment request){
        assignmentService.uploadAssignment(header, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "(오현) 운영진 과제 제출한 아기사자 리스트 조회", description = "경로로 과제id를 받아서 그 과제를 제출한 아기사자 리스트 보기",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 오류"),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자일 경우"),
                    @ApiResponse(responseCode = "404", description = "해당 과제에 대한 제출된 과제가 없을 경우")})
    @GetMapping("/checklions/{assignmentId}")
    public ResponseEntity<List<SubmittedAssignmentLion>> getSubmittedAssignmentLions(HttpServletRequest header, @PathVariable Long assignmentId){
        List<SubmittedAssignmentLion> lionList = assignmentService.getSubmittedAssignmentLion(header, assignmentId);
        return ResponseEntity.status(HttpStatus.OK).body(lionList);
    }

    @Operation(summary = "(오현) 운영진 제출된 과제 채점", description = "제출된 과제 id, 피드백 내용, passNonePass 여부 받아서 채점",
            responses = {@ApiResponse(responseCode = "200", description = "채점 및 피드백 수정 완료, 이메일 보내기 완료"),
                    @ApiResponse(responseCode = "401", description = "토큰 오류"),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자일 경우"),
                    @ApiResponse(responseCode = "404", description = "해당 과제에 대한 제출된 과제가 없을 경우"),
                    @ApiResponse(responseCode = "502", description = "이메일 전송 실패")})
    @PutMapping("/check/feedback")
    public ResponseEntity<String> checkOrFeedbackSummitedAssignment(HttpServletRequest header, @RequestBody CheckSubmittedAssignment request){
        assignmentService.checkOrFeedbackSubmittedAssignment(header, request);
        return ResponseEntity.ok("채점 및 피드백 완료. 이메일 전송됨");
    }

    @Operation(summary = "(오현) 운영진 과제 채점 상세페이지 조회", description = "과제 제목, 과제 설명, 피드백 반환",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 오류"),
                    @ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자일 경우"),
                    @ApiResponse(responseCode = "404", description = "해당 과제에 대한 제출된 과제가 없을 경우")})
    @GetMapping("/check/{submitAssignmentId}")
    public ResponseEntity<FeedbackDetailRes> getFeedbackDetail(HttpServletRequest header, @PathVariable Long submitAssignmentId){
        FeedbackDetailRes feedbackDetailRes =  assignmentService.getFeedbackDetail(header, submitAssignmentId);
        return ResponseEntity.status(HttpStatus.OK).body(feedbackDetailRes);
    }
}
