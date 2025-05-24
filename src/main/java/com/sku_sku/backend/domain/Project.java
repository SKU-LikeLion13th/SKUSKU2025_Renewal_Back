package com.sku_sku.backend.domain;

import com.sku_sku.backend.service.FileUtility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 프로젝트
public class Project {
    @Id @GeneratedValue
    private Long id; // pk

    private String classTh; // 프로젝트 만든 기수 예) 13th 확장성 때문에 enum으로 안 하고 String으로

    private String title; // 프로젝트 제목

    private String subTitle; // 프로젝트 부제목

    private String projectUrl; // 프로젝트 url

    private String imageName; // 프로젝트 이미지 이름

    private String imageType; // 프로젝트 이미지 타입

    private String imageUrl; // 프로젝트 이미지 // CDN URL

    private String imageKey; // 프로젝트 이미지 저장된 경로

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 강의 자료 생성일


    // 생성자
    public Project(String classTh, String title, String subTitle, String projectUrl, String imageName, String imageType, String imageUrl, String imageKey) {
        this.classTh = classTh;
        this.title = title;
        this.subTitle = subTitle;
        this.projectUrl = projectUrl;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.createDate = LocalDateTime.now(); // 생성 당시 시간

    }

    // 업데이트 (이미지 포함)
    public void changeProject(String classTh, String title, String subTitle, String projectUrl, String imageName, String imageType, String imageUrl, String imageKey) {
        this.classTh = classTh;
        this.title = title;
        this.subTitle = subTitle;
        this.projectUrl = projectUrl;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.createDate = LocalDateTime.now(); // 수정 당시 시간
    }
}
