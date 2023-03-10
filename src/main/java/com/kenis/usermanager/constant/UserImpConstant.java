package com.kenis.usermanager.constant;

import com.kenis.usermanager.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This class contains constants that are used in the implementation of the {@link UserService} and {@link UserDetailsService} interfaces.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
public class UserImpConstant {
    /** The constant for the message "Username already exists". */
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    /** The constant for the message "Email already exists". */
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    /** The constant for the message "No user found by username". */
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username ";
    /** The constant for the message "returning found user by username". */
    public static final String RETURNING_FOUND_USER_BY_USERNAME = "returning found user by username: ";

    /** The constant for the message "No User Found for email". */
    public static final String NO_USER_FOUND_BY_EMAIL = "No User Found for email: ";
    public static final String NO_USER_FOUND_BY_IDENTIFIER = "No User Found by this Identifier: ";
    public static final String BLANK_FIELD_MESSAGE = " a required field and cannot be empty or blank";

    //TODO add noBlank noEmpty for all user filed
}