package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.assignment.Assignment;
import com.sku_sku.backend.enums.TrackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTrackType(TrackType trackType);
}
