package com.sku_sku.backend.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private final String email;

    public CustomOAuth2User(String email, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        super(authorities, attributes, "email"); // "email"을 기본 키로 설정
        this.email = email;
    }
}
