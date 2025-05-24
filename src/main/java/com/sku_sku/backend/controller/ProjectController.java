package com.sku_sku.backend.controller;

import com.sku_sku.backend.dto.Response.ProjectDTO;
import com.sku_sku.backend.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "공개적 기능 : 프로젝트 관련")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "(민규) 모든 Project 정보 조회", description = "",
            responses = {@ApiResponse(responseCode = "200", description = "모든 프로젝트 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "Project 하나도 없")})
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDTO.ProjectRes>> findProjectAll() {
        List<ProjectDTO.ProjectRes> projectAllField = projectService.findProjectAllIdDesc();
        if (projectAllField.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(projectAllField);
    }
}
