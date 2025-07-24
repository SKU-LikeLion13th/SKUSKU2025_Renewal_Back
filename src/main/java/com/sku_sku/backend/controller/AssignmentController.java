package com.sku_sku.backend.controller;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.dto.Request.SubmitAssignmentDTO;
import com.sku_sku.backend.dto.Response.AssignmentDTO;
import com.sku_sku.backend.dto.Response.AssignmentDTO.AssignmentDetail;
import com.sku_sku.backend.dto.Response.AssignmentDTO.AssignmentRes;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.service.assignment.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assignment")
@Tag(name = "아기사자 기능: 과제 관련")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(summary = "(오현) 아기사자 과제 제출", description = "과제 id, 내용, 파일 제출",
            responses = {@ApiResponse(responseCode = "201", description = "제출 완료"),
                    @ApiResponse(responseCode = "401", description = "토큰 오류"),
                    @ApiResponse(responseCode = "404", description = "해당 과제가 없는 경우")})
    @PostMapping("/submit")
    public ResponseEntity<Void> submitAssignment(@AuthenticationPrincipal Lion lion, @RequestBody SubmitAssignmentDTO.SubmitAssignment request){
        assignmentService.submitAssignment(lion, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "(오현) 제출된 과제 삭제", description = "제출된 과제 id 받음",
            responses = {@ApiResponse(responseCode = "204", description = "과제 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 오류"),
                    @ApiResponse(responseCode = "404", description = "해당 과제가 없을 경우")})
    @DeleteMapping("/delete/{submitAssignmentId}")
    public ResponseEntity<Void> deleteSubmitAssignment(@PathVariable Long submitAssignmentId){
        assignmentService.deleteSubmittedAssignment(submitAssignmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "(오현) 제출된 과제 업데이트", description = "제출된 과제 id, content, file 받음",
            responses = {@ApiResponse(responseCode = "204", description = "과제 업데이트 성공"),
                    @ApiResponse(responseCode = "401", description = "토큰 오류"),
                    @ApiResponse(responseCode = "404", description = "해당 과제가 없을 경우")})
    @PutMapping("/update")
    public ResponseEntity<Void> updateSubmitAssignment(@RequestBody SubmitAssignmentDTO.UpdateSubmitAssignment req){
        assignmentService.updateSubmitAssignment(req);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "(오현) 아기사자 트랙별 과제 조회", description = "경로로 트랙타입을 받아서 트랙별 모든 과제 조회",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "현재 트랙에 과제가 없을 경우"),
                    @ApiResponse(responseCode = "400", description = "trackType이 올바르지 않은 경우")})
    @GetMapping("/track/{trackType}")
    public ResponseEntity<List<AssignmentRes>> getAllAssignmentByTrack(@AuthenticationPrincipal Lion lion, @PathVariable TrackType trackType){
        List<AssignmentRes> assignmentList = assignmentService.getAssignment(lion, trackType);
        return ResponseEntity.status(HttpStatus.OK).body(assignmentList);
    }

    @Operation(summary = "(오현) 아기사자 트랙별 과제 상세 조회", description = "경로로 트랙타입을 받아서 트랙별 모든 과제 조회",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 과제가 없을 경우"),
                    @ApiResponse(responseCode = "400", description = "assignmentId가 올바르지 않은 경우")})
    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentDetail> getAssignmentDetail(@PathVariable Long assignmentId){
        AssignmentDetail assignmentDetail = assignmentService.getAssignmentDetail(assignmentId);
        return ResponseEntity.status(HttpStatus.OK).body(assignmentDetail);
    }

    @Operation(summary = "(오현) 아기사자 제출한 과제 조회", description = "경로로 해당 과제id를 받아서 본인이 제출한 과제 조회",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 과제가 없을 경우 or 제출한 과제가 없을경우")})
    @GetMapping("/submit/{assignmentId}")
    public ResponseEntity<AssignmentDTO.SubmitAssigmentRes> getSubmitAssignment(@AuthenticationPrincipal Lion lion, @PathVariable Long assignmentId){
        AssignmentDTO.SubmitAssigmentRes  submitAssigmentRes = assignmentService.getSubmitAssignment(lion, assignmentId);
        return ResponseEntity.status(HttpStatus.OK).body(submitAssigmentRes);
    }

}
