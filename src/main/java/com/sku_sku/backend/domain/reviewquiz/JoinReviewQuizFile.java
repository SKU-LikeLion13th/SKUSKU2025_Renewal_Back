package com.sku_sku.backend.domain.reviewquiz;

import com.sku_sku.backend.domain.assignment.Assignment;
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
    private ReviewQuiz reviewQuiz; // 복습 퀴즈

    @Lob @Column(name = "file", columnDefinition = "LONGBLOB")
    private byte[] file; // 첨부한 파일

    public JoinReviewQuizFile(ReviewQuiz reviewQuiz, byte[] file) {
        this.reviewQuiz = reviewQuiz;
        this.file = file;
    }
}
