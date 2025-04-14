package com.sku_sku.backend.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 과제 피드백
public class Feedback {
    @Id
    @GeneratedValue
    private Long id; // pk

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_assignment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubmitAssignment submitAssignment; // 피드백할 과제

    private String content; // 피드백 내용

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 피드백 생성일

    public Feedback(SubmitAssignment submitAssignment, String content){
        this.submitAssignment=submitAssignment;
        this.content=content;
        this.createDate=LocalDateTime.now();
    }

    public void updateFeedback(String content){
        this.content=content;
    }
}
