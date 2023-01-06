package com.kenis.supportportal.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;



@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HttpResponse {
    private Integer httpStatusCode; // 200, 201
    private HttpStatus httpStatus ;
    private String reason ;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "CET")
    private Date timeStamp;

    public HttpResponse(Integer httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }
}
