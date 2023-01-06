package com.kenis.supportportal.constant;


/**
 * This class defines constants for different roles in the application and the corresponding authorities that each role has.
 * The authority is a string representation of a specific permission that a role has.
 * @example: the USER_AUTHORITIES array has only one element "user:read" which means that users with this role have the permission to read user information.
 * The HR_AUTHORITIES array has two elements "user:read" and "user:update" which means that users with this role have the permission to read and update user information.
 * Similarly, the other roles have their respective authorities.
 */
public class Authority {
    /** The authority to read user data */
    public static final String[] USER_AUTHORITIES = {"user:read"};
    /** The authority to read and update user data */
    public static final String[] HR_AUTHORITIES = {"user:read", "user:update" };
    /** The authority to read and update user data */
    public static final String[] MANAGER_AUTHORITIES = {"user:read", "user:update"};
    /** The authority to read, update, and create user data */
    public static final String[] ADMIN_AUTHORITIES = {"user:read", "user:update", "user:create"};
    /** The authority to read, update, create, and delete user data */
    public static final String[] SUPER_ADMIN_AUTHORITIES = {"user:read", "user:update", "user:create", "user:delete"};
}
