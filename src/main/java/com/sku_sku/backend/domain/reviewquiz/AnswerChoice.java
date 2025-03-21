package com.sku_sku.backend.domain.reviewquiz;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor
@Entity // 복습 퀴즈 문제 보기
public class AnswerChoice {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_quiz_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReviewQuiz reviewQuiz; // 해당 복습 퀴즈 문제

    private String content; // 보기 내용
}
