package com.kenis.usermanager.exception.domain;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kenis.usermanager.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

/**
 * This class is a @RestControllerAdvice that handles exceptions that may occur in the application.
 * It has a number of exception handler methods that handle specific types of exceptions and return a standardized ResponseEntity with a HttpResponse object as the body.
 * The HttpResponse object contains information about the HTTP status code, the reason for the status, and a message.
 * The exception handler methods are called whenever the corresponding exception is thrown in the application.
 * @author Mohamed Ali KENIS
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandling implements ErrorController {
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    public static final String ERROR_PATH = "/error";

    //= Privet Methods == // Should be "Private Methods"

    /**
     * Creates a {@code ResponseEntity} containing a standardized {@code HttpResponse}.
     * This private helper method is used by other exception handlers to ensure consistent response formats.
     *
     * @param httpStatus The {@link HttpStatus} for the response.
     * @param message    The custom message to include in the response body.
     * @return A {@code ResponseEntity<HttpResponse>} configured with the given status and message.
     */
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpResponse httpResponse = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(httpResponse,httpStatus);
    }

    //= Public Methods ==

    /**
     * Handles {@link DisabledException}, thrown when a user attempts to authenticate with a disabled account.
     *
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code BAD_REQUEST} and a message indicating the account is disabled.
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException (){
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED );
    }

    /**
     * Handles {@code BadCredentialsException} and returns a standardized {@code HttpResponse} with an {@code UNAUTHORIZED} status.
     * This exception is typically thrown when the provided username or password is incorrect.
     *
     * @return A {@code ResponseEntity<HttpResponse>} containing the {@code HttpResponse}.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(UNAUTHORIZED, INCORRECT_CREDENTIALS); // Corrected status to UNAUTHORIZED as per typical behavior for bad credentials
    }

    /**
     * Handles {@link AccessDeniedException}, thrown when a user attempts to access a resource without sufficient permissions.
     *
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code FORBIDDEN} and a message indicating insufficient permissions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    /**
     * Handles {@link LockedException}, thrown when a user attempts to authenticate with a locked account.
     *
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code UNAUTHORIZED} and a message indicating the account is locked.
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    /**
     * Handles {@link TokenExpiredException}, thrown when a JWT token provided by the user has expired.
     *
     * @param exception The caught {@code TokenExpiredException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code UNAUTHORIZED} and the exception's message.
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    /**
     * Handles {@link EmailExistException}, thrown when an attempt is made to use an email that already exists in the system.
     *
     * @param exception The caught {@code EmailExistException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code BAD_REQUEST} and a message indicating the email already exists.
     */
    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Handles {@link UsernameExistException}, thrown when an attempt is made to use a username that already exists in the system.
     *
     * @param exception The caught {@code UsernameExistException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code BAD_REQUEST} and a message indicating the username already exists.
     */
    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Handles {@link EmailNotFoundException}, thrown when a specified email is not found in the system.
     *
     * @param exception The caught {@code EmailNotFoundException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code BAD_REQUEST} and the exception's message.
     */
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Handles {@link UserNotFoundException}, thrown when a specified user is not found in the system.
     *
     * @param exception The caught {@code UserNotFoundException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code BAD_REQUEST} and the exception's message.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Handles {@code BlankFieldException} and returns a standardized {@code HttpResponse} with a {@code BAD_REQUEST} status,
     * including the exception message.
     *
     * @param exception The caught {@code BlankFieldException}.
     * @return A {@code ResponseEntity} containing the {@code HttpResponse}.
     */
    @ExceptionHandler(BlankFieldException.class)
    public ResponseEntity<HttpResponse> blankFieldException(BlankFieldException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * Handles {@link HttpRequestMethodNotSupportedException}, thrown when an unsupported HTTP method is used for an endpoint.
     *
     * @param exception The caught {@code HttpRequestMethodNotSupportedException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code METHOD_NOT_ALLOWED} and a message specifying allowed methods.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    /**
     * Handles generic {@link Exception} instances, serving as a catch-all for unhandled exceptions.
     * Logs the error and returns a standardized {@code HttpResponse} with status {@code INTERNAL_SERVER_ERROR}.
     *
     * @param exception The caught {@code Exception}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code INTERNAL_SERVER_ERROR} and a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    /**
     * Handles {@link NoResultException}, typically thrown when a database query expecting a single result finds none.
     * Logs the error and returns a standardized {@code HttpResponse} with status {@code NOT_FOUND}.
     *
     * @param exception The caught {@code NoResultException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code NOT_FOUND} and the exception's message.
     */
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    /**
     * Handles {@link IOException}, related to input/output operations.
     * Logs the error and returns a standardized {@code HttpResponse} with status {@code INTERNAL_SERVER_ERROR}.
     *
     * @param exception The caught {@code IOException}.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code INTERNAL_SERVER_ERROR} and a message indicating a file processing error.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);

    }

    /**
     * Handles requests to the "/error" path, typically for unmapped URLs, returning a 404 Not Found response.
     *
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code NOT_FOUND} for unmapped URLs.
     */
    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, "There is no mapping for this URL");
    }

    /**
     * Returns the configured error path for the application.
     * This is used by Spring Boot's error handling mechanisms.
     *
     * @return The error path string (e.g., "/error").
     */
    public String getErrorPath() {
        return ERROR_PATH;
    }

}
