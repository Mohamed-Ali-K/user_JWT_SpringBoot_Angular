package com.kenis.usermanager.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kenis.usermanager.domain.HttpResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.kenis.usermanager.constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * JwtAuthenticationEntryPoint is a class that extends Http403ForbiddenEntryPoint, which is a Spring Security class that handles
 * requests that fail to be authenticated. When an AuthenticationException is thrown, it sends a 403 response.
 * <p>
 * This class is used to handle cases where the user is not authenticated (not logged in) and attempts to access a protected resource.
 * In such cases, it sends a custom response with a status of 403 (FORBIDDEN) and a message indicating that the user needs to log in to access the page.
 *
 * @author Kenis Mohamed Ali
 * @see Http403ForbiddenEntryPoint
 */
@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    /**
     * This method is called when an authenticated user attempts to access a resource that they do not have permission to
     * access. It sends an HTTP 403 FORBIDDEN response back to the user.
     *
     * @param request   the HTTP request
     * @param response  the HTTP response
     * @param exception the exception thrown due to the unauthorized access
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        HttpResponse httpResponse = new HttpResponse(
                FORBIDDEN.value(),
                FORBIDDEN,
                FORBIDDEN.getReasonPhrase().toUpperCase(),
                FORBIDDEN_MESSAGE
        );
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
