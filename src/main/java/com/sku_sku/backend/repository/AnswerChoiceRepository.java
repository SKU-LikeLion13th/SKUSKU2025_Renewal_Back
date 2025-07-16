package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.reviewquiz.AnswerChoice;
import com.sku_sku.backend.domain.reviewquiz.ReviewQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerChoiceRepository extends JpaRepository<AnswerChoice, Integer> {
    List<AnswerChoice> findByReviewQuiz(ReviewQuiz reviewQuiz);
}
