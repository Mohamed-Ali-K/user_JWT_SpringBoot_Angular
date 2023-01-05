package com.kenis.supportportal.domain;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HttpResponse {
    private Integer httpStatusCode; // 200, 201
    private HttpStatus httpStatus ;
    private String reason ;
    private String message;
}
