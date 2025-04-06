package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewQuizResponseRepository extends JpaRepository<ReviewQuizResponse, Long> {
    ReviewQuizResponse findReviewQuizResponseByLionAndReviewQuiz(Lion lion, ReviewQuiz reviewQuiz);
}
