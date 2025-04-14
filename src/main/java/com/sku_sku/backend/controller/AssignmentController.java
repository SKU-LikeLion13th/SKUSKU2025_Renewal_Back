package com.sku_sku.backend.controller;

import com.sku_sku.backend.dto.Request.AssignmentDTO;
import com.sku_sku.backend.dto.Response.AssignmentDTO.AssignmentRes;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> submitAssingment(HttpServletRequest header, AssignmentDTO.SubmitAssignment request) throws IOException {
        assignmentService.saveSubmittedAssignment(header, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "(오현) 아기사자 트랙별 과제 조회", description = "경로로 트랙타입을 받아서 트랙별 모든 과제 조회",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "현재 트랙에 과제가 없을 경우"),
                    @ApiResponse(responseCode = "400", description = "trackType이 올바르지 않은 경우")})
    @GetMapping("/{trackType}")
    public ResponseEntity<List<AssignmentRes>> getAllAssignmentByTrack(HttpServletRequest header, @PathVariable TrackType trackType){
        List<AssignmentRes> assignmentList = assignmentService.getAssignment(header, trackType);
        return ResponseEntity.status(HttpStatus.OK).body(assignmentList);
    }

}
