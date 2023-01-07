package com.kenis.supportportal.exception.domain;

/**
 * Exception to be thrown when the specified user could not be found in the database.
 */
public class UserNotFoundException extends Exception {

    /**
     * Constructs a new exception with the specified message.
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
