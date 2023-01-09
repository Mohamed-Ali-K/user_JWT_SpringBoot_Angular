package com.kenis.supportportal.exception.domain;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kenis.supportportal.domain.HttpResponse;
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

    //= Privet Methods ==

    /**
     * The createHttpResponse method is a private helper method that creates ResponseEntity object . The HttpResponse object contains the following fields:
     *
     * @value: an int representing the status code value
     * @status: an HttpStatus enum representing the status code
     * @reasonPhrase: a String representation of the reason phrase associated with the status code
     * @message: a String representing a custom message provided as an argument to the method
     * This method is used as a helper method in several other exception handler methods in the ExceptionHandling class to create and return a response to the client with the appropriate HttpStatus code and message in the body.
     * @param httpStatus is an enumeration of HTTP status codes, as defined in the HTTP specification. It includes constants for all the common HTTP status codes, such as 200 (OK), 404 (Not Found), and 500 (Internal Server Error). These constants can be used to set the status code of an HTTP response, which can be useful for returning specific error messages or other information to the client.
     * @param message parameter is a string representing the message to be included in the response body
     * @return ResponseEntity object with a HttpResponse body and a specified HttpStatus code.
     *
     * @author Mohamed Ali KENIS
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
     * The method accountDisabledException() handles the DisabledException, which is thrown when a user attempts to authenticate with a disabled account
     * @return ResponseEntity with a BAD_REQUEST status code and a message saying that the account has been disabled.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException (){
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED );
    }

    /**
     * The method badCredentialsException() handles the BadCredentialsException
     * This exception is thrown when the provided username or password is incorrect.
     * @return ResponseEntity with a BAD_REQUEST status code and a message saying that the username or password is incorrect.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    /**
     * The accessDeniedException() method handles the AccessDeniedException
     * @return ResponseEntity with a FORBIDDEN status code and a message saying that the user does not have enough permission.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    /**
     * The lockedException() method handles the LockedException
     * @return ResponseEntity with a UNAUTHORIZED status code and a message saying that the account has been locked.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    /**
     * The tokenExpiredException(TokenExpiredException exception) method handles the TokenExpiredException.
     * which is thrown when the JWT token provided by the user has expired.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return ResponseEntity with a UNAUTHORIZED status code and the exception's message.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    /**
     * The emailExistException(EmailExistException exception) method handles the EmailExistException, which is thrown when the provided email already exists in the system.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return The method returns a ResponseEntity object with a status code of BAD_REQUEST (400) and a message indicating that the email already exists in the system.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     *The usernameExistException(UserNameExistException exception) method handles the UserNameExistException, which is thrown when the provided username already exists in the system.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return The method returns a ResponseEntity object with a status code of BAD_REQUEST (400) and a message indicating that the username already exists in the system.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * The emailNotFoundException(EmailNotFoundException exception) method handles the EmailNotFoundException.
     * This exception is thrown when the provided email is not found in the system.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return ResponseEntity with a BAD_REQUEST status code and the exception's message.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * The userNotFoundException() method is used to handle the UserNotFoundException.
     * When this exception is thrown, it indicates that the provided username was not found in the system.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return This method returns a ResponseEntity object with a BAD_REQUEST status code and a message containing the exception message.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    /**
     * The methodNotSupportedException() method handles the HttpRequestMethodNotSupportedException.
     * This exception is thrown when the request method being used (e.g. GET, POST, etc.) is not supported on the requested endpoint.
     * The method creates a response with a METHOD_NOT_ALLOWED status code and a message indicating which request method is allowed on the endpoint.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return  ResponseEntity with a METHOD_NOT_ALLOWED status code and a message specifying the request method that is allowed on the endpoint.
     * The message is constructed by formatting the METHOD_IS_NOT_ALLOWED string with the supported method obtained from the exception.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    /**
     * This method is an exception handler for any type of exception that occurs in the application.
     * It logs the error message and returns a response with an HTTP status of INTERNAL_SERVER_ERROR and a message saying
     * "An error occurred while processing the request".
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return "An error occurred while processing the request" .
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    /**
     * This method is an exception handler for the NoResultException class.
     * It is used to handle an exception that is thrown when a query returns no result.
     * When this exception is thrown, it will log the error message and return a response with an HTTP status of NOT_FOUND and the exception message as the body.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return response with an HTTP status of NOT_FOUND and the exception message as the body.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    /**
     * This method handles exceptions to type java.io.IOException.
     * It logs the error message and returns a ResponseEntity with a HttpResponse object containing an HTTP status code of INTERNAL_SERVER_ERROR and a message of ERROR_PROCESSING_FILE.
     * This method is likely used to handle exceptions that occur when there is an error reading or writing to a file.
     * @param exception is an event, which occurs during the execution of a program, that disrupts the normal flow of the program's instructions. When an exception occurs, the normal flow of the program is interrupted and the program/system terminates abnormally. Exceptions are used to handle errors and other exceptional events that occur in a program, allowing the program to terminate gracefully and providing useful debugging information.
     * @return ResponseEntity with a HttpResponse object containing an HTTP status code of INTERNAL_SERVER_ERROR and a message of ERROR_PROCESSING_FILE.
     * @author Mohamed Ali KENIS
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);

    }

    /**
     * This method is an exception handler for the "404 Not Found" HTTP error.
     * When this error occurs, this method will be called and The response will be sent to the client.
     * @return response with an HTTP status of "404 Not Found" and a message saying "There is no mapping for this URL".
     * @author Mohamed Ali KENIS
     */
    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, "There is no mapping for this URL");
    }

    /**
     * This method returns the path of the error page. It is used to handle errors that occur during the execution of an application.
     * This method is typically used in conjunction with the @RequestMapping annotation to map a specific URL to this error page.
     * @return value, "/error", is the path of the error page.
     * @author Mohamed Ali KENIS
     */
    public String getErrorPath() {
        return ERROR_PATH;
    }

}
