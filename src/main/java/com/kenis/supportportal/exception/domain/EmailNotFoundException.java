package com.kenis.supportportal.exception.domain;

/**
 * Exception to be thrown when an email address that is being searched for cannot be found in the system.
 */
public class EmailNotFoundException extends Exception {

    /**
     * Constructs a new EmailNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public EmailNotFoundException(String message) {
        super(message);
    }
}
