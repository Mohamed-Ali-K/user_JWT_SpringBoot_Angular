package com.kenis.supportportal.exception.domain;

/**
 * Exception thrown when a user attempts to create an account with an email that is already in use.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
public class EmailExistException extends Exception{

    /**
     * Constructs a new EmailExistException with the specified message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public EmailExistException(String message) {
        super(message);
    }
}
