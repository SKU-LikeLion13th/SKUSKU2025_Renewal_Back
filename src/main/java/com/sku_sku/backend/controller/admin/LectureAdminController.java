package com.sku_sku.backend.controller.admin;

import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.dto.Request.LectureDTO;
import com.sku_sku.backend.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/lecture")
@Tag(name = "관리자 기능: 강의자료 관련")
public class LectureAdminController {
    private final LectureService lectureService;

    @Operation(summary = "(민규) 강의자료 추가", description = "body에 form-data로 강의자료의 trackType, title, files 필요",
            responses = {@ApiResponse(responseCode = "201", description = "강의자료 생성 성공")})
    @PostMapping("/add")
    public ResponseEntity<String> uploadFiles(HttpServletRequest header,
                                               LectureDTO.createLectureRequest request) throws IOException {
        lectureService.createLecture(header, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("강의자료 생성 성공");
    }

    @Operation(summary = "(민규) 강의자료 수정", description = "body에 form-data로 강의자료 id와 수정하고 싶은 값만 넣으면 됨",
            responses = {@ApiResponse(responseCode = "201", description = "강의자료 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "그 id에 해당하는 값 없")})
    @PutMapping("/update")
    public ResponseEntity<String> updateLecture(HttpServletRequest header,
                                                 LectureDTO.updateLectureRequest request) throws IOException {
        lectureService.updateLecture(header, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("강의자료 수정 성공");
    }

    @Operation(summary = "(민규) 강의자료 삭제", description = "경로 변수로 삭제할 강의자료의 id 필요",
            responses = {@ApiResponse(responseCode = "200", description = "강의자료 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "그 id에 해당하는 값 없")})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLecture(@PathVariable("id") Long lectureId) {
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.status(HttpStatus.OK).body("강의자료 삭제 성공");
    }

}
