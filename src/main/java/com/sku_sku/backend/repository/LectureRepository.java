package com.sku_sku.backend.repository;


import com.sku_sku.backend.domain.lecture.Lecture;
import com.sku_sku.backend.enums.TrackType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // id로 강의 안내물 반환 (강의자료를 제외한)
    @EntityGraph(attributePaths = {"joinLectureFile"})
    Optional<Lecture> findById(Long id);

    // 전체 강의 안내물 리스트 반환 (강의자료를 제외한)
    List<Lecture> findAll();

    // 트랙별 강의 안내물 리스트 내림차순 반환 (강의자료를 제외한)
    List<Lecture> findByTrackOrderByIdDesc(TrackType trackType);

    Optional<List<Lecture>> findByTrackOrderByCreateDateTimeDesc(TrackType trackType);
}
