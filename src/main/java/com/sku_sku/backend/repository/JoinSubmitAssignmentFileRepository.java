package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.assignment.JoinSubmitAssignmentFile;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinSubmitAssignmentFileRepository extends JpaRepository<JoinSubmitAssignmentFile, Long> {

    void deleteAllBySubmitAssignment(SubmitAssignment submitAssignment);
}
