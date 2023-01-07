package com.kenis.supportportal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;


/**
 * A simple Java bean that represents an HTTP response.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HttpResponse {
    /**
     * The HTTP status code of the response.
     */
    private Integer httpStatusCode; // 200, 201

    /**
     * The 'HttpStatus' enum value that corresponds to the HTTP status code.
     */
    private HttpStatus httpStatus ;

    /**
     * A short description of the HTTP status code.
     */
    private String reason ;

    /**
     * A custom message for the response.
     */
    private String message;

    /**
     * The timestamp of the response.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "CET")
    private Date timeStamp;

    /**
     * Constructs a new HttpResponse with the specified HTTP status code, 'HttpStatus' enum value, reason, and message.
     * The 'timeStamp' field is initialized with the current date and time.
     *
     * @param httpStatusCode the HTTP status code of the response
     * @param httpStatus the 'HttpStatus' enum value that corresponds to the HTTP status code
     * @param reason a short description of the HTTP status code
     * @param message a custom message for the response
     */
    public HttpResponse(Integer httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }
}
