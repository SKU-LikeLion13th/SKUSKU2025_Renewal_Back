package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.assignment.JoinAssignmentFile;
import com.sku_sku.backend.domain.assignment.JoinSubmitAssignmentFile;
import com.sku_sku.backend.domain.assignment.SubmitAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinSubmitAssignmentFileRepository extends JpaRepository<JoinSubmitAssignmentFile, Long> {
    List<JoinSubmitAssignmentFile> findBySubmitAssignment(SubmitAssignment submitAssignment);
    Optional<JoinSubmitAssignmentFile> findByFileKey(String fileKey);
    void deleteAllBySubmitAssignmentAndFileKeyIn(SubmitAssignment submitAssignment, List<String> fileKey);
}
