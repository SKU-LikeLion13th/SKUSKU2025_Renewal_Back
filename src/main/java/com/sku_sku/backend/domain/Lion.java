package com.sku_sku.backend.domain;

import com.sku_sku.backend.domain.enums.Role;
import com.sku_sku.backend.domain.enums.Track;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity // 사자
public class Lion {
    @Id @GeneratedValue
    private Long lionId; // pk
    private String name; // 이름
    private String email; // 이메일

    @Enumerated(EnumType.STRING)
    private Track track; // 트랙 BACKEND or FRONTEND or DESIGN

    @Enumerated(EnumType.STRING)
    private Role role; // 권한 ADMIN_LION or BABY_LION or LEGACY_LION
}
