package com.sku_sku.backend.domain.assignment;

import com.sku_sku.backend.enums.AllowedFileType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 운영진이 과제를 낼 때 첨부한 파일
public class JoinAssignmentFile {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Assignment assignment; // 과제

    private String fileName; // 과제 첨부 파일 이름

    @Enumerated(EnumType.STRING)
    private AllowedFileType fileType; // 과제 첨부 파일 타입

    private Long fileSize; // 과제 첨부 파일 사이즈

    private String fileUrl; // 과제 첨부 파일 자료 // CDN URL

    private String fileKey; // 과제 첨부 파일 저장된 경로

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 과제 첨부 파일 생성일

    private LocalDateTime updateDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 과제 첨부 파일 수정일

    public JoinAssignmentFile(Assignment assignment, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey) {
        this.assignment = assignment;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.createDate = LocalDateTime.now();
    }
    public void updateJoinAssignmentFile(Assignment assignment, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey) {
        this.assignment = assignment;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.updateDate = LocalDateTime.now();
    }
}
