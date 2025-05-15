package com.sku_sku.backend.domain.reviewquiz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private String fileUrl; // 퀴즈 첨부 자료 // CDN URL

    private String fileName; // 퀴즈 첨부 자료 이름

    private String fileType; // 퀴즈 첨부 자료 타입

    private Long fileSize; // 퀴즈 첨부 자료 사이즈

    public JoinReviewQuizFile(ReviewQuiz reviewQuiz, String fileUrl, String fileName, String fileType, Long fileSize) {
        this.ReviewQuiz = reviewQuiz;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}
