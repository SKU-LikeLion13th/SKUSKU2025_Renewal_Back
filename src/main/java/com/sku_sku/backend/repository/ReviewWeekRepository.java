package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.reviewquiz.ReviewWeek;
import com.sku_sku.backend.enums.TrackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewWeekRepository extends JpaRepository<ReviewWeek, Long> {
    List<ReviewWeek> findReviewWeekByTrackType(TrackType trackType);
    ReviewWeek findReviewWeekById(long reviewWeekId);
}
