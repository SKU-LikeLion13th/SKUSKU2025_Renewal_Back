package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.Project;
import com.sku_sku.backend.dto.Request.ProjectDTO;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.exception.InvalidTitleException;
import com.sku_sku.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sku_sku.backend.dto.Response.ProjectDTO.ProjectAllField;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Transactional
    public void addProject(ProjectDTO.ProjectCreateRequest request) {
        Project project = new Project(request.getClassTh(),
                request.getTitle(),
                request.getSubTitle(),
                request.getUrl(),
                request.getImageUrl());
        projectRepository.save(project);
    }

    @Transactional
    public void updateProject(ProjectDTO.ProjectUpdateRequest req) {
        Project project = projectRepository.findById(req.getId())
                .orElseThrow(() -> new InvalidIdException("project"));

        String newClassTh = getOrDefault(req.getClassTh(), project.getClassTh());
        String newTitle = getOrDefault(req.getTitle(), project.getTitle());
        String newSubTitle = getOrDefault(req.getSubTitle(), project.getSubTitle());
        String newUrl = getOrDefault(req.getUrl(), project.getUrl());
        String newImageUrl = getOrDefault(req.getImageUrl(), project.getImageUrl());

        project.changeProject(newClassTh, newTitle, newSubTitle, newUrl, newImageUrl);
    }

    private <T> T getOrDefault(T newOne, T previousOne) {
        return newOne != null ? newOne : previousOne;
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException("project"));
        projectRepository.delete(project);
    }

    public List<ProjectAllField> findProjectAllIdDesc() {
        List<Project> projects = projectRepository.findAllByOrderByIdDesc();

        return projects.stream()
                .map(project -> new ProjectAllField(
                        project.getId(),
                        project.getClassTh(),
                        project.getTitle(),
                        project.getSubTitle(),
                        project.getUrl(),
                        project.getImageUrl()
                ))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ProjectAllField findProjectById(Long id) {
        return projectRepository.findById(id)
                .map(project -> new ProjectAllField(
                        project.getId(),
                        project.getClassTh(),
                        project.getTitle(),
                        project.getSubTitle(),
                        project.getUrl(),
                        project.getImageUrl()))
                .orElseThrow(() -> new InvalidIdException("project"));
    }
}
