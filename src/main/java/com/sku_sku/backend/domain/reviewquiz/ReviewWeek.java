package com.sku_sku.backend.domain.reviewquiz;

import com.sku_sku.backend.enums.TrackType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity // 복습 퀴즈 주차
public class ReviewWeek {
    @Id
    @GeneratedValue
    private Long id; // pk

    @Enumerated(EnumType.STRING)
    private TrackType trackType; // 트랙 BACKEND or FRONTEND or DESIGN

    private String title; // 복습 퀴즈 제목

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 복습 퀴즈 생성일

    private LocalDateTime updateDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 복습 퀴즈 수정일

    public ReviewWeek(TrackType trackType, String title) {
        this.trackType = trackType;
        this.title = title;
        this.createDate = LocalDateTime.now();
        this.updateDate = this.createDate;
    }

    public void update(TrackType trackType, String title) {
        this.trackType = trackType;
        this.title = title;
        this.updateDate = LocalDateTime.now();
    }
}
