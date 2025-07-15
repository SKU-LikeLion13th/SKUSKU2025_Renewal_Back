package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.domain.assignment.JoinAssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinAssignmentFileRepository extends JpaRepository<JoinAssignmentFile, Long> {
    List<JoinAssignmentFile> findByAssignment(Assignment assignment);

    Optional<JoinAssignmentFile> findByFileKey(String fileKey);
}
