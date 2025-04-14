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

    @Lob @Column(name = "file", columnDefinition = "LONGBLOB")
    private byte[] file; // 첨부한 파일

    public JoinReviewQuizFile(ReviewQuiz reviewQuiz, byte[] file) {
        this.ReviewQuiz = reviewQuiz;
        this.file = file;
    }
}
