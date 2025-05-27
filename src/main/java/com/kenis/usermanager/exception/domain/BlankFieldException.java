package com.kenis.usermanager.exception.domain;

/**
 * Exception thrown when a required field is found to be blank or empty during validation.
 * This indicates that input data is missing necessary information.
 */
public class BlankFieldException extends Exception{
    public BlankFieldException(String message) {
        super(message);
    }
}
