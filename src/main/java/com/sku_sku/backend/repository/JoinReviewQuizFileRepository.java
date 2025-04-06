package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.reviewquiz.JoinReviewQuizFile;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinReviewQuizFileRepository extends JpaRepository<JoinReviewQuizFile, Long> {
    List<JoinReviewQuizFile> findByReviewQuiz(ReviewQuiz review);
}
