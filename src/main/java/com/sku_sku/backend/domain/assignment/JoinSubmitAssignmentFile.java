package com.sku_sku.backend.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor
@Entity // 아기사자가 과제를 제출할 때 첨부한 파일
public class JoinSubmitAssignmentFile {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_assignment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubmitAssignment submitAssignment; // 제출할 과제

    @Lob @Column(name = "file", columnDefinition = "LONGBLOB")
    private byte[] file; // 첨부한 파일

    public JoinSubmitAssignmentFile(SubmitAssignment submitAssignment, byte[] file){
        this.submitAssignment=submitAssignment;
        this.file=file;
    }

}
