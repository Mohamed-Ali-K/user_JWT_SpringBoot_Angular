package com.kenis.supportportal.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


public class UserPrincipal implements UserDetails {
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(user.getAuthorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //TODO add this to user model
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO also add this to user model
    }

    @Override
    public boolean isEnabled() {
        return user.getActive();
    }
}
