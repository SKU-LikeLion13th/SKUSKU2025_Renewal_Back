package com.sku_sku.backend.repository;


import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinLectureFilesRepository extends JpaRepository<JoinLectureFile, Long> {

    // 강의 안내물로 강의자료 삭제
    void deleteByLecture(Lecture lecture);

    List<JoinLectureFile> findByLecture(Lecture lecture);

    List<JoinLectureFile> findByLectureId(Long lectureId);

    void deleteAllByLectureAndFileKeyIn(Lecture lecture, List<String> fileKeys);


}
