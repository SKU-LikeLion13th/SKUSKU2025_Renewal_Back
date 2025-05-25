package com.sku_sku.backend.controller;

import com.sku_sku.backend.dto.Response.LectureDTO.ResponseLecture;
import com.sku_sku.backend.dto.Response.LectureDTO.ResponseLectureWithoutFiles;
import com.sku_sku.backend.enums.TrackType;
import com.sku_sku.backend.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture")
@Tag(name = "아기사자 기능: 강의자료 관련")
public class LectureController {
    private final LectureService lectureService;

    @Operation(summary = "(민규) id로 강의자료 개별 조회", description = "경로 변수로 강의자료의 id 필요",
            responses = {@ApiResponse(responseCode = "200", description = "조회를 하면 강의 trackType, 제목, 작성자, 조회수, 작성 시간, 수정 시간, 강의자료 나옴"),
                    @ApiResponse(responseCode = "404", description = "그 id에 해당하는 값 없")})
    @GetMapping("/{id}")
    public ResponseEntity<ResponseLecture> findLectureById(@PathVariable("id") Long lectureId) {
        ResponseLecture responseLecture = lectureService.findLectureById(lectureId);
        return ResponseEntity.status(HttpStatus.OK).body(responseLecture);
    }

    @Operation(summary = "(민규) 트랙별 모든 강의자료 조회 without file", description = "경로 변수로 트랙 타입 필요(BACKEND or FRONTEND or DESIGN)",
            responses = {@ApiResponse(responseCode = "200", description = "트랙별 모든 강의 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "강의 자료 하나도 없")})
    @GetMapping("/all/{track}")
    public ResponseEntity<List<ResponseLectureWithoutFiles>> getAllLectureByTrack(@PathVariable("track") TrackType track) {
        List<ResponseLectureWithoutFiles> lectureFiles = lectureService.findAllLectureByTrack(track);
        return ResponseEntity.ok(lectureFiles);
    }
}
