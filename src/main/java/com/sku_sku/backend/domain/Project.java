package com.sku_sku.backend.domain;

import com.sku_sku.backend.service.FileUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Getter
@NoArgsConstructor
@Entity // 프로젝트
public class Project {
    @Id @GeneratedValue
    private Long id; // pk

    private String classTh; // 프로젝트 만든 기수 예) 13th 확장성 때문에 enum으로 안 하고 String으로

    private String title; // 프로젝트 제목

    private String subTitle; // 프로젝트 부제목

    private String url; // 프로젝트 url

    private String imageUrl; // 프로젝트 이미지 // CDN URL

    // 생성자
    public Project(String classTh, String title, String subTitle, String url, String imageUrl) {
        this.classTh = classTh;
        this.title = title;
        this.subTitle = subTitle;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    // 업데이트 (이미지 포함)
    public void changeProject(String classTh, String title, String subTitle, String url, String imageUrl) {
        this.classTh = classTh;
        this.title = title;
        this.subTitle = subTitle;
        this.url = url;
        this.imageUrl = imageUrl;
    }

//    // 프로젝트 이미지 인코딩
//    public String arrayToImage() {
//        return FileUtility.encodeFile(this.image);
//    }
//
//    // 이미지 업데이트
//    public void setImage(MultipartFile image) throws IOException {
//        this.image = image.getBytes();
//    }
}
