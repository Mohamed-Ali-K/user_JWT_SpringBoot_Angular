package com.kenis.supportportal.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenis.supportportal.domain.HttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.kenis.supportportal.constant.SecurityConstant.ACCESS_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * This class is a handler for handling access denied exceptions when a user attempts to access a resource that
 * they do not have permission to access. It sends a JSON response with a status code of 401 (UNAUTHORIZED) and
 * a message indicating that access is denied.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles the given access denied exception by creating a JSON response and setting the
     * response status to 401 (UNAUTHORIZED).
     *
     * @param request the request that caused the exception
     * @param response the response to send to the client
     * @param exception the exception that was thrown
     * @throws IOException if an I/O error occurs while handling the exception
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception) throws IOException {
        HttpResponse httpResponse = new HttpResponse(
                UNAUTHORIZED.value(),
                UNAUTHORIZED,
                UNAUTHORIZED.getReasonPhrase().toUpperCase(),
                ACCESS_DENIED_MESSAGE
        );
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
