package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import com.sku_sku.backend.enums.TrackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewQuizRepository extends JpaRepository<ReviewQuiz, Long> {

    @Query("SELECT rq FROM ReviewQuiz rq WHERE rq.reviewWeek.trackType = :trackType AND rq.reviewWeek.id = :reviewWeekId")
    List<ReviewQuiz> findByTrackTypeAndReviewWeek(@Param("trackType") TrackType trackType, @Param("reviewWeekId") Long reviewWeekId);
}
