package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.enums.TrackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LionRepository extends JpaRepository<Lion, Long> {

    // 이메일로 사자 반환
    Optional<Lion> findByEmail(String email);
    List<Lion> findAllLionsByEmail(String email);

    // 트랙으로 사자 반환
    List<Lion> findWritersByTrackType(TrackType trackType);

    // 트랙과 역할로 사자 반환
    List<Lion> findWritersByTrackTypeAndRoleType(TrackType trackType, RoleType roleType);

}
