package com.kenis.supportportal.filter;


import com.kenis.supportportal.utility.JWTTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.kenis.supportportal.constant.SecurityConstant.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

/**
 * This class represents a filter that handles authorization for the application.
 * It checks for the presence of a valid JWT in the request header and verifies its authenticity.
 * If the JWT is valid, it allows the request to proceed. Otherwise, it returns an access denied response.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;

    /**
     * Creates a new instance of the JwtAuthorizationFilter class.
     *
     * @param jwtTokenProvider The JWT token provider.
     */
    public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Handles the request and response for the application.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param filterChain The filter chain.
     * @throws ServletException If an error occurs while processing the request.
     * @throws IOException If an error occurs while writing the response.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String userName = jwtTokenProvider.getSubject(token);
            if (jwtTokenProvider.isTokenValid(userName, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(userName, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
