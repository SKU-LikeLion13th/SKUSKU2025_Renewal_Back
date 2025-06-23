package com.sku_sku.backend.domain.reviewquiz;

import com.sku_sku.backend.enums.AllowedFileType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 운영진이 리뷰퀴즈를 낼 때 첨부한 파일
public class JoinReviewQuizFile {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewquiz_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReviewQuiz ReviewQuiz; // 과제

    private String fileName; // 강의 자료 이름

    @Enumerated(EnumType.STRING)
    private AllowedFileType fileType; // 강의 자료 타입

    private Long fileSize; // 퀴즈 첨부 파일 사이즈

    private String fileUrl; // 퀴즈 첨부 파일  // CDN URL

    private String fileKey; // 퀴즈 첨부 파일 저장된 경로

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 퀴즈 첨부 파일 생성일

    private LocalDateTime updateDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 퀴즈 첨부 파일 수정일

    public JoinReviewQuizFile(ReviewQuiz reviewQuiz, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey) {
        this.ReviewQuiz = reviewQuiz;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.createDate = LocalDateTime.now(); // 생성 당시 시간
    }

    public void updateJoinReviewQuizFile(ReviewQuiz reviewQuiz, String fileName, AllowedFileType fileType, Long fileSize, String fileUrl, String fileKey) {
        this.ReviewQuiz = reviewQuiz;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileKey = fileKey;
        this.updateDate = LocalDateTime.now(); // 수정 당시 시간
    }
}
