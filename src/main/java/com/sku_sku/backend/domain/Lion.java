package com.sku_sku.backend.domain;

import com.sku_sku.backend.enums.RoleType;
import com.sku_sku.backend.enums.TrackType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity // 사자
public class Lion implements UserDetails {
    @Id @GeneratedValue
    private Long id; // pk

    private String name; // 사자 이름

    private String email; // 사자 이메일

    @Enumerated(EnumType.STRING)
    private TrackType trackType; // 트랙 BACKEND or FRONTEND or DESIGN

    @Enumerated(EnumType.STRING)
    private RoleType roleType; // 권한 ADMIN_LION or BABY_LION

    // 생성자
    public Lion(String name, String email, TrackType trackType, RoleType roleType) {
        this.name = name;
        this.email = email;
        this.trackType = trackType;
        this.roleType = roleType;
    }

    // 업데이트
    public void update(String name, String email, TrackType trackType, RoleType roleType) {
        this.name = name;
        this.email = email;
        this.trackType = trackType;
        this.roleType = roleType;
    }

    // UserDetails 필수 impl
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호 인증 안 쓸 경우 null
    }

    @Override
    public String getUsername() {
        return email; // Security 식별자
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
