package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.assignment.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findBySubmitAssignmentId(Long submitAssignmentId);
}
