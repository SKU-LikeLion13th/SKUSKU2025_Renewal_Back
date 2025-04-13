package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmitAssignmentRepository extends JpaRepository<SubmitAssignment, Long> {

    Optional<SubmitAssignment> findByAssignmentAndLionId(Assignment assignment, Long lionId);

    List<SubmitAssignment> findByAssignmentId(Long assignmentId);
}
