package com.sku_sku.backend.domain.reviewquiz;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.enums.AnswerStatus;
import com.sku_sku.backend.enums.QuizType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter

@NoArgsConstructor
@Entity // 복습 퀴즈 응답
public class ReviewQuizResponse {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lion_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lion lion; // 복습 퀴즈 푼 아기사자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_quiz_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ReviewQuiz reviewQuiz; // 푼 복습 퀴즈

    private String answer; // 작성한 답

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 복습 퀴즈 제출한 시간
    @Setter
    private LocalDateTime updateDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 복습 퀴즈 다시 푼 시간
    @Setter
    private AnswerStatus answerStatus; // 정답 여부 TRUE or FALSE or EMPTY(주관식인 경우)

    private QuizType quizType; // 문제 유형 MULTIPLE_CHOICE or ESSAY_QUESTION

    @Setter
    private int count = 1; // 다시 푼 횟수 // 맨 처음 응답은 다시 푼 횟수가 아니라서 기본 값 0으로

    public ReviewQuizResponse(Lion lion, ReviewQuiz reviewQuiz, String answer, QuizType quizType) {
        this.lion = lion;
        this.reviewQuiz = reviewQuiz;
        this.answer = answer;
        this.createDate = LocalDateTime.now();
        this.updateDate = this.createDate;
        this.quizType = quizType;
    }
}
