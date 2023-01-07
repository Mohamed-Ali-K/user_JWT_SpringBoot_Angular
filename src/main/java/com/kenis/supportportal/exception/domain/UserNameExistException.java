package com.kenis.supportportal.exception.domain;

/**
 * Exception to be thrown when a user attempts to create an account with a
 * username that is already taken.
 */
public class UserNameExistException extends Exception{

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     */
    public UserNameExistException(String message) {
        super(message);
    }
}
