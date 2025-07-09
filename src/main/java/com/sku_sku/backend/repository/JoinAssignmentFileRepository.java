package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.domain.assignment.JoinAssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinAssignmentFileRepository extends JpaRepository<JoinAssignmentFile, Long> {
    List<JoinAssignmentFile> findByAssignment(Assignment assignment);
}
