package com.sku_sku.backend.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor
@Entity // 과제로 제출한 파일
public class JoinAssignmentFile {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_assignment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubmitAssignment submitAssignment; // 제출한 과제

    @Lob @Column(name = "file", columnDefinition = "LONGBLOB")
    private byte[] file; // 제출한 파일
}
