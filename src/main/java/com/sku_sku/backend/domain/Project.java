package com.sku_sku.backend.domain;

import com.sku_sku.backend.enums.AllowedFileType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private AllowedFileType imageType; // 프로젝트 이미지 타입

    private Long imageSize; // 프로젝트 이미지 사이즈

    private String imageUrl; // 프로젝트 이미지 // CDN URL

    private String imageKey; // 프로젝트 이미지 저장된 경로

    private LocalDateTime createDateTime; // YYYY-MM-DD HH:MM:SS.nnnnnn // 강의 자료 생성일

    private LocalDateTime updateDateTime; // YYYY-MM-DD HH:MM:SS.nnnnnn // 강의 자료 수정일


    // 생성자
    public Project(String classTh, String title, String subTitle, String projectUrl, String imageName, AllowedFileType imageType, Long imageSize, String imageUrl, String imageKey) {
        this.classTh = classTh;
        this.title = title;
        this.subTitle = subTitle;
        this.projectUrl = projectUrl;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageSize = imageSize;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.createDateTime = LocalDateTime.now(); // 생성 당시 시간
        this.updateDateTime = null; // 생성 당시 시간

    }

    // 업데이트 (이미지 포함)
    public void changeProject(String classTh, String title, String subTitle, String projectUrl, String imageName, AllowedFileType imageType, Long imageSize, String imageUrl, String imageKey) {
        this.classTh = classTh;
        this.title = title;
        this.subTitle = subTitle;
        this.projectUrl = projectUrl;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageSize = imageSize;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.updateDateTime = LocalDateTime.now(); // 수정 당시 시간
    }
}
