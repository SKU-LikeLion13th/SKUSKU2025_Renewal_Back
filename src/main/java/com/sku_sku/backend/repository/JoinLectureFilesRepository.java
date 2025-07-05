package com.sku_sku.backend.repository;


import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinLectureFilesRepository extends JpaRepository<JoinLectureFile, Long> {

    // 강의 안내물로 강의자료 삭제
    void deleteByLecture(Lecture lecture);

    Optional<JoinLectureFile> findByFileKey(String fileKey);

    List<JoinLectureFile> findByLecture(Lecture lecture);

    void deleteAllByLectureAndFileKeyIn(Lecture lecture, List<String> fileKeys);


}
