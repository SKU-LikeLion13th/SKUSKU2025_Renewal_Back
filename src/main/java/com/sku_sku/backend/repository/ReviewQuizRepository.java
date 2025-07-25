package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import com.sku_sku.backend.domain.reviewquiz.ReviewWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewQuizRepository extends JpaRepository<ReviewQuiz, Long> {

    //@Query("SELECT rq FROM ReviewQuiz rq WHERE rq.reviewWeek.id = :reviewWeekId")
    List<ReviewQuiz> findByReviewWeek(ReviewWeek reviewWeek);

    Optional<ReviewQuiz> findById(Long id);
}
