package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.assignment.JoinSubmitAssignmentFile;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinSubmitAssignmentFileRepository extends JpaRepository<JoinSubmitAssignmentFile, Long> {
    List<JoinSubmitAssignmentFile> findBySubmitAssignment(SubmitAssignment submitAssignment);
    void deleteAllBySubmitAssignment(SubmitAssignment submitAssignment);
}
