package com.team8.project2.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SecurityUser extends User {

    @Getter
    private long id;

    public SecurityUser(long id, String memberId, String password, Collection<? extends GrantedAuthority> authorities) {
        super(memberId, password, authorities);
        this.id = id;
    }
}
