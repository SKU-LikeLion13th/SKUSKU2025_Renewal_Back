package com.sku_sku.backend.domain.assignment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor
@Entity // 운영진이 과제를 낼 때 첨부한 파일
public class JoinAssignmentFile {
    @Id
    @GeneratedValue
    private Long id; // pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Assignment assignment; // 과제

    @Lob @Column(name = "file", columnDefinition = "LONGBLOB")
    private byte[] file; // 첨부한 파일
}
