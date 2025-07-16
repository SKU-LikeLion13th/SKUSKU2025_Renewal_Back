package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.reviewquiz.ReviewWeek;
import com.sku_sku.backend.enums.TrackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewWeekRepository extends JpaRepository<ReviewWeek, Long> {
    Optional<List<ReviewWeek>> findReviewWeekByTrackType(TrackType trackType);
    Optional<ReviewWeek> findReviewWeekById(long reviewWeekId);
}
