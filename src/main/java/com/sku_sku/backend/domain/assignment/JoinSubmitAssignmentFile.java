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
@Entity // 아기사자가 과제를 제출할 때 첨부한 파일
public class JoinSubmitAssignmentFile {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_assignment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubmitAssignment submitAssignment; // 제출할 과제

    private String fileName; // 제출한 과제 자료 이름

    @Enumerated(EnumType.STRING)
    private AllowedFileType fileType; // 제출한 과제 자료 타입

    private Long fileSize; // 제출한 과제 자료 사이즈

    private String fileUrl; // 제출한 과제 자료 // CDN URL

    private String fileKey; // 제출한 과제 자료 저장된 경로

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 제출한 과제 자료 생성일

    private LocalDateTime updateDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 제출한 과제 자료 수정일

    private Boolean isUpdate; //수정 여부 // true or false

    public JoinSubmitAssignmentFile(SubmitAssignment submitAssignment, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey, boolean isUpdate){
        this.submitAssignment = submitAssignment;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.isUpdate = isUpdate;
        this.createDate = LocalDateTime.now(); // 생성 당시 시간
    }

    public void updateJoinSubmitAssignmentFile(SubmitAssignment submitAssignment, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey) {
        this.submitAssignment = submitAssignment;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.updateDate = LocalDateTime.now(); // 수정 당시 시간
    }
}
