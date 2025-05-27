package com.kenis.usermanager.domain;

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
    private final User user;

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

    /**
     * Returns the username of the authenticated user.
     * @return The username.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     * @return {@code true} if the user's account is valid (i.e., non-expired), {@code false} otherwise.
     * Always returns {@code true} in this implementation.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; //TODO add this to user model
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked account cannot be authenticated.
     * @return {@code true} if the user is not locked, {@code false} otherwise.
     * Delegates to {@code user.getIsNotLocked()}.
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getIsNotLocked();
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent authentication.
     * @return {@code true} if the user's credentials are valid (i.e., non-expired), {@code false} otherwise.
     * Always returns {@code true} in this implementation.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO also add this to user model
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
     * @return {@code true} if the user is enabled, {@code false} otherwise.
     * Delegates to {@code user.getIsActive()}.
     */
    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }
}
