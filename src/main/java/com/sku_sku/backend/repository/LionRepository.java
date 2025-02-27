package com.sku_sku.backend.repository;

import com.sku_sku.backend.domain.Lion;
import com.sku_sku.backend.domain.enums.Role;
import com.sku_sku.backend.domain.enums.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LionRepository extends JpaRepository<Lion, Long> {

    // 이메일로 사자 반환
    Optional<Lion> findByEmail(String email);
    List<Lion> findAllLionsByEmail(String email);

    // 트랙으로 사자 반환
    List<Lion> findWritersByTrack(Track track);

    // 트랙과 역할로 사자 반환
    List<Lion> findWritersByTrackAndRole(Track track, Role role);

}
