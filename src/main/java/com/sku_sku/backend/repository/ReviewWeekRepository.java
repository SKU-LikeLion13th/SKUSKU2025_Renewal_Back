package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.reviewquiz.ReviewWeek;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewWeekRepository extends JpaRepository<ReviewWeek, Long> {
    //void saveReviewWeek(ReviewWeek reviewWeek);
}
