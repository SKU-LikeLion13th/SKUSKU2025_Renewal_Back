package com.sku_sku.backend.domain.reviewquiz;

import com.sku_sku.backend.enums.QuizType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@Entity // 복습 퀴즈 문제
public class ReviewQuiz {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_week_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReviewWeek reviewWeek; // 복습 퀴즈 주차

    private String content; // 문제 내용

    private String explanation; // 문제 해설

    private String answer; // 문제 정답

    private QuizType quizType; // 문제 유형 MULTIPLE_CHOICE or ESSAY_QUESTION

    public ReviewQuiz(ReviewWeek reviewWeek, String content, String explanation, String answer, QuizType quizType) {
        this.reviewWeek = reviewWeek;
        this.content = content;
        this.explanation = explanation;
        this.answer = answer;
        this.quizType = quizType;
    }
}
