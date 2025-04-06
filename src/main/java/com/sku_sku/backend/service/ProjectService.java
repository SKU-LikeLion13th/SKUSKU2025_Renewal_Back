package com.sku_sku.backend.service;


import com.sku_sku.backend.domain.Project;
import com.sku_sku.backend.exception.InvalidIdException;
import com.sku_sku.backend.exception.InvalidTitleException;
import com.sku_sku.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // @PostMapping("/admin/project/add")
    @Transactional
    public void addProject(String classTh, String title, String subTitle, String url, MultipartFile image) throws IOException {
        if (projectRepository.findByTitle(title).isPresent()) {
            throw new InvalidTitleException();
        }
        byte[] imageBytes = image.getBytes();
        Project project = new Project(classTh, title, subTitle, url, imageBytes);
        projectRepository.save(project);
    }

    // @PutMapping("/admin/project/update")
    @Transactional
    public void updateProject(Long id, String classTh, String title, String subTitle, String url, MultipartFile image) throws IOException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException("project"));
        if (image != null && !image.isEmpty()) {
            project.setImage(image);
        }
        String newClassTh = (classTh != null && !classTh.isEmpty() ? classTh : project.getClassTh());
        String newTitle = (title != null && !title.isEmpty() ? title : project.getTitle());
        if (!newTitle.equals(project.getTitle()) && projectRepository.findByTitle(title).isPresent()) {
            throw new InvalidTitleException();
        }
        String newSubTitle = (subTitle != null && !subTitle.isEmpty() ? subTitle : project.getSubTitle());
        String newUrl = (url != null && !url.isEmpty() ? url : project.getUrl());
        project.changeProject(newClassTh, newTitle, newSubTitle, newUrl);
    }

    // @DeleteMapping("/admin/project")
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
                        project.arrayToImage() // 이미지 바이트 배열을 Base64 문자열로 변환
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
                        project.arrayToImage()))
                .orElseThrow(() -> new InvalidIdException("project"));
    }
}
