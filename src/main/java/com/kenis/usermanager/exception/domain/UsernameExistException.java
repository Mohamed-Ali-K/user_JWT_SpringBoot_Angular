package com.kenis.usermanager.exception.domain;

/**
 * Exception to be thrown when a user attempts to create an account with a
 * username that is already taken.
 */
public class UsernameExistException extends Exception{

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     */
    public UsernameExistException(String message) {
        super(message);
    }
}
