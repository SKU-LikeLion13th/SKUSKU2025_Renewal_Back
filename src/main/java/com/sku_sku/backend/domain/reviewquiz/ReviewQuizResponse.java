package com.sku_sku.backend.domain.reviewquiz;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.enums.AnswerStatus;
import com.sku_sku.backend.enums.QuizType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private LocalDateTime responseDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 복습 퀴즈 제출한 시간

    private AnswerStatus answerStatus; // 정답 여부 TRUE or FALSE or EMPTY(주관식인 경우)

    private QuizType quizType; // 문제 유형 MULTIPLE_CHOICE or ESSAY_QUESTION or FILE_ATTACHMENT
}
