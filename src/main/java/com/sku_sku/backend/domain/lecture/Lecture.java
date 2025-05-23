package com.sku_sku.backend.domain.lecture;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sku_sku.backend.enums.TrackType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity // 강의 안내물
public class Lecture {
    @Id @GeneratedValue
    private Long id; // pk가

    @Enumerated(EnumType.STRING)
    private TrackType track; // 트랙 BACKEND or FRONTEND or DESIGN

    private String title; // 강의 안내물 제목

    private String content; // 강의 안내물 내용

    private String writer; // 강의 안내물 작성자

    @JsonManagedReference
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JoinLectureFile> joinLectureFile = new ArrayList<>(); // 강의 자료

    private LocalDateTime createDate; // YYYY-MM-DD HH:MM:SS.nnnnnn // 강의 안내물 생성일

    // 생성자
    public Lecture(TrackType track, String title, String content, String writer) {
        this.track = track;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createDate = LocalDateTime.now(); // 생성 당시 시간
    }

    // 업데이트
    public void update(TrackType track, String title, String content, String writer) {
        this.track = track;
        this.title = title;
        this.content = content;
        this.writer = writer;
    }
}
