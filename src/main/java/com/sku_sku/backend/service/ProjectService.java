package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.Project;
import com.sku_sku.backend.dto.Request.ProjectDTO;
import com.sku_sku.backend.enums.AllowedFileType;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sku_sku.backend.dto.Response.ProjectDTO.ProjectRes;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;

    @Transactional
    public void addProject(ProjectDTO.ProjectCreateRequest req) {
        Project project = new Project(req.getClassTh(),
                req.getTitle(),
                req.getSubTitle(),
                req.getProjectUrl(),
                req.getImageName(),
                req.getImageType(),
                req.getFileSize(),
                req.getImageUrl(),
                req.getImageKey());
        projectRepository.save(project);
    }

    @Transactional
    public void updateProject(ProjectDTO.ProjectUpdateRequest req) {
        Project project = projectRepository.findById(req.getId())
                .orElseThrow(() -> new InvalidIdException("project"));

        String newClassTh = getOrDefault(req.getClassTh(), project.getClassTh());
        String newTitle = getOrDefault(req.getTitle(), project.getTitle());
        String newSubTitle = getOrDefault(req.getSubTitle(), project.getSubTitle());
        String newProjectUrl = getOrDefault(req.getProjectUrl(), project.getProjectUrl());

        String newImageName = getOrDefault(req.getImageName(), project.getImageName());
        AllowedFileType newImageType = getOrDefault(req.getImageType(), project.getImageType());
        Long newImageSize = getOrDefault(req.getFileSize(), project.getImageSize());
        String newImageUrl = getOrDefault(req.getImageUrl(), project.getImageUrl());
        String newImageKey = getOrDefault(req.getImageKey(), project.getImageKey());

        // 이전 이미지와 다를 경우 기존 S3 이미지 삭제
        if (!newImageKey.equals(project.getImageKey())) {
            s3Service.deleteFiles(List.of(project.getImageKey()));
        }

        project.changeProject(newClassTh, newTitle, newSubTitle, newProjectUrl, newImageName, newImageType, newImageSize, newImageUrl, newImageKey);
    }

    private <T> T getOrDefault(T newOne, T previousOne) {
        return newOne != null ? newOne : previousOne;
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException("project"));

        // S3 이미지 삭제
        if (project.getImageKey() != null && !project.getImageKey().isBlank()) {
            s3Service.deleteFiles(List.of(project.getImageKey()));
        }

        projectRepository.delete(project);
    }

    public List<ProjectRes> findProjectAllIdDesc() {
        List<Project> projects = projectRepository.findAllByOrderByIdDesc();

        return projects.stream()
                .map(project -> new ProjectRes(
                        project.getId(),
                        project.getClassTh(),
                        project.getTitle(),
                        project.getSubTitle(),
                        project.getProjectUrl(),
                        project.getImageName(),
                        project.getImageType(),
                        project.getImageSize(),
                        project.getImageUrl(),
                        project.getImageKey()
                ))
                .toList();
    }

    public ProjectRes findProjectById(Long id) {
        return projectRepository.findById(id)
                .map(project -> new ProjectRes(
                        project.getId(),
                        project.getClassTh(),
                        project.getTitle(),
                        project.getSubTitle(),
                        project.getProjectUrl(),
                        project.getImageName(),
                        project.getImageType(),
                        project.getImageSize(),
                        project.getImageUrl(),
                        project.getImageKey()
                ))
                .orElseThrow(() -> new InvalidIdException("project"));
    }
}
