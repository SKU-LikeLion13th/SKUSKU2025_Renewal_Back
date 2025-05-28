package com.sku_sku.backend.domain.lecture;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sku_sku.backend.enums.AllowedFileType;
import com.sku_sku.backend.service.FileUtility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity // 강의 자료
public class JoinLectureFile {
    @Id @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture; // 해당 강의 안내물

    private String fileName; // 강의 자료 이름

    private AllowedFileType fileType; // 강의 자료 타입

    private Long fileSize; // 강의 자료 사이즈

    private String fileUrl; // 강의 자료 // CDN URL

    private String fileKey; // 강의 자료 저장된 경로

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 강의 자료 생성일

    private LocalDateTime updateDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 강의 자료 수정일

    // 생성자
    public JoinLectureFile(Lecture lecture, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey) {
        this.lecture = lecture;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.createDate = LocalDateTime.now(); // 생성 당시 시간
    }

    public void updateJoinLectureFile(Lecture lecture, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey) {
        this.lecture = lecture;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.updateDate = LocalDateTime.now(); // 수정 당시 시간
    }
}
