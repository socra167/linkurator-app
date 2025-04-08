package com.team8.project2.global.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SecurityUser extends User {

    private final long id;

    public SecurityUser(long id, String memberId, String password, Collection<? extends GrantedAuthority> authorities) {
        super(memberId, password, authorities);
        this.id = id;
    }

    // TODO: 명시적 getter 추가
    public long getId() {
        return id;
    }
}
