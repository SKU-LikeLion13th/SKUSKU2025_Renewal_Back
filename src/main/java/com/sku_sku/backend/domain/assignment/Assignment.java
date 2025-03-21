package com.sku_sku.backend.domain.assignment;

import com.sku_sku.backend.enums.QuizType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 과제
public class Assignment {
    @Id
    @GeneratedValue
    private Long id; // pk

    private String title; // 과제 제목

    private String description; // 과제 설명

    private QuizType quizType; // 과제 제출 유형 // 주관식이랑 파일첨부만 사용 ESSAY_QUESTION or FILE_ATTACHMENT

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 과제 생성일
}
