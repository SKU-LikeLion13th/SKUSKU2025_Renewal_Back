package com.sku_sku.backend.domain.assignment;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.enums.PassNonePass;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 제출한 과제
public class SubmitAssignment {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Assignment assignment; // 해당 과제

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lion_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lion lion; // 제출한 아기사자

    private String content; // 제출한 내용

    private PassNonePass passNonePass; // 과제 통과 여부 PASS or NONE_PASS

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 과제 제출 시점

    private LocalDateTime updateDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 과제 수정 시점
}
