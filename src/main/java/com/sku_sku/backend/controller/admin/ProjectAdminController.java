package com.sku_sku.backend.controller.admin;

import com.sku_sku.backend.domain.Project;
import com.sku_sku.backend.dto.Request.ProjectDTO;
import com.sku_sku.backend.dto.Request.ProjectDTO.ProjectCreateRequest;
import com.sku_sku.backend.dto.Request.ProjectDTO.ProjectUpdateRequest;
import com.sku_sku.backend.dto.Response.ProjectDTO.ProjectAllField;
import com.sku_sku.backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/project")
//@PreAuthorize("hasRole('ADMIN_LION')")
@Tag(name = "관리자 기능: 프로젝트 관련")
public class ProjectAdminController {

    private final ProjectService projectService;

    @Operation(summary = "(민규) Project 추가", description = "body에 form-data로 Project의 title, subTitle, image 필요",
            responses = {@ApiResponse(responseCode = "201", description = "프로젝트 생성 성공"),
                    @ApiResponse(responseCode = "409", description = "그 title 이미 있")})
    @PostMapping("/add")
    public ResponseEntity<String> addProject(ProjectCreateRequest request) throws IOException {
            projectService.addProject(
                    request.getClassTh(),
                    request.getTitle(),
                    request.getSubTitle(),
                    request.getUrl(),
                    request.getImage());
            return ResponseEntity.status(HttpStatus.CREATED).body("프로젝트 생성 성공");
    }

    @Operation(summary = "(민규) Project 수정", description = "body에 form-data로 Project의 id와 수정하고 싶은 값만 넣으면 됨",
            responses = {@ApiResponse(responseCode = "200", description = "프로젝트 수정 성공"),
                    @ApiResponse(responseCode = "409", description = "그 title 이미 있"),
                    @ApiResponse(responseCode = "404", description = "그 id에 해당하는 값 없")})
    @PutMapping("/update")
    public ResponseEntity<String> updateProject(ProjectUpdateRequest request) throws IOException {
            projectService.updateProject(
                    request.getId(),
                    request.getClassTh(),
                    request.getTitle(),
                    request.getSubTitle(),
                    request.getUrl(),
                    request.getImage());
            return ResponseEntity.status(HttpStatus.OK).body("프로젝트 수정 성공");
    }

    @Operation(summary = "(민규) id로 Project 개별 정보 조회", description = "경로 변수로 Project의 id 필요",
            responses = {@ApiResponse(responseCode = "200", description = "조회를 하면 프로젝트 id, 제목, 프로젝트 부제목, 프로젝트 사진이 출력."),
                    @ApiResponse(responseCode = "404", description = "그 id에 해당하는 값 없")})
    @GetMapping("/{id}")
    public ResponseEntity<ProjectAllField> findProjectById(@PathVariable("id") Long projectId) {
        ProjectAllField projectAllField = projectService.findProjectById(projectId);
            return ResponseEntity.status(HttpStatus.OK).body(projectAllField);
    }

    @Operation(summary = "(민규) Project 삭제", description = "경로 변수로 Project의 id 필요",
              responses = {@ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "404", description = "그 id에 해당하는 값 없")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long projectId) {
            projectService.deleteProject(projectId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
