package com.kenis.supportportal.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * A class representing a user's principal in the application's security system. This class implements the
 * <p>
 * {@link UserDetails} interface to provide information about the user's authorities and security-related properties.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
public class UserPrincipal implements UserDetails {
    private User user;

    /**
     * Creates a new {@code UserPrincipal} instance with the given {@link User} object.
     *
     * @param user the user object
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Returns the granted authorities for the user.
     *
     * @return the granted authorities for the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(user.getAuthorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns the password for the user.
     * @return the password for the user
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //TODO add this to user model
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getIsNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO also add this to user model
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }
}
