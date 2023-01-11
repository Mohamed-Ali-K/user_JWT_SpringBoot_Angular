package com.kenis.usermanager.enumeration;

import static com.kenis.usermanager.constant.Authority.*;

/**
 * This enum represents the different roles that a user can have.
 * Each role has a set of authorities associated with it.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
public enum Role {
    /**
     * The role for a regular user
     */
    ROLE_USER(USER_AUTHORITIES),
    /**
     * The role for a human resources user
     */
    ROLE_HR(HR_AUTHORITIES),
    /**
     * The role for a manager
     */
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    /**
     * The role for an administrator
     */
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    /**
     * The role for a super administrator
     */
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    /**
     * The authorities associated with this role
     */
    private final String[] authorities;

    /**
     * Constructs a new role with the given authorities.
     *
     * @param authorities the authorities for this role
     */
    Role(String[] authorities) {
        this.authorities = authorities;
    }

    /**
     * Returns the authorities associated with this role.
     *
     * @return the authorities for this role
     */
    public String[] getAuthorities() {
        return authorities;
    }
}
