package com.sku_sku.backend.repository;


import com.sku_sku.backend.domain.lecture.JoinLectureFile;
import com.sku_sku.backend.domain.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinLectureFilesRepository extends JpaRepository<JoinLectureFile, Long> {

    // 강의 안내물로 강의자료 삭제
    void deleteByLecture(Lecture lecture);
}
