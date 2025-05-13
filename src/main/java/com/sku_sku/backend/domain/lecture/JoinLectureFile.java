package com.sku_sku.backend.domain.lecture;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sku_sku.backend.service.FileUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 강의 자료
public class JoinLectureFile {
    @Id @GeneratedValue
    private Long id; // pk

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture; // 해당 강의 안내물

    private String fileUrl; // 강의 자료 // CDN URL

    private String fileName; // 강의 자료 이름

    private String fileType; // 강의 자료 타입

    private Long fileSize; // 강의 자료 사이즈

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 강의 자료 생성일

    // 생성자
    public JoinLectureFile(Lecture lecture, String fileName, String fileUrl, String fileType, Long fileSize) {
        this.lecture = lecture;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.createDate = LocalDateTime.now(); // 생성 당시 시간
    }
}
