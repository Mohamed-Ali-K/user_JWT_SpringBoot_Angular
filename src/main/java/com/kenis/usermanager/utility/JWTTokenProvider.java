package com.kenis.usermanager.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import static com.kenis.usermanager.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.kenis.usermanager.domain.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that provides utility methods for generating and verifying JWT tokens.
 *
 * The goal of this class is to provide methods for generating and verifying JWT tokens to be used for authentication and authorization in a Spring application.
 * @author Mohamed Ali Kenis
 */
@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    //= Private Methods ==

    /**
     * Returns an array of claims extracted from the given JWT token.
     *
     * @param token the JWT token
     * @return an array of claims extracted from the given JWT token
     */
    private String[]  getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier
                .verify(token)
                .getClaim(AUTHORITIES)
                .asArray(String.class);
    }

    /**
     * Returns a JWTVerifier object that can be used to verify the given JWT token.
     *
     * @return a JWTVerifier object that can be used to verify the given JWT token
     */
    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try{
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT
                    .require(algorithm)
                    .withIssuer(GET_ARRAY_LLC)
                    .build();
        }catch(JWTVerificationException exception){
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }

    /**
     * Returns an array of claims extracted from the given user principal.
     *
     * @param user the user principal
     * @return an array of claims extracted from the given user principal
     */
    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

    /**
     * Returns true if the given JWT token has expired, false otherwise.
     *
     * @param verifier the JWTVerifier object used to verify the JWT token
     * @param token the JWT token
     * @return true if the given JWT token has expired, false otherwise
     */
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    //= Public Methods ==

    /**
     * Generates a JWT token for the given user principal.
     *
     * @param userPrincipal the user principal
     * @return a JWT token for the given user principal
     */
    public String generateJwtToken(UserPrincipal userPrincipal){
        String[] claim = getClaimsFromUser(userPrincipal);
        return JWT
                .create()
                .withIssuer(GET_ARRAY_LLC)
                .withAudience(GET_ARRAY_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claim)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIM))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    /**
     * Returns a list of GrantedAuthority objects extracted from the given JWT token.
     *
     * @param token the JWT token
     * @return a list of GrantedAuthority objects extracted from the given JWT token
     */
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns an {@link Authentication} object based on the given user name and granted authorities.
     *
     * @param userName the username
     * @param authorities the granted authorities
     * @param request the HTTP servlet request
     * @return an {@link Authentication} object based on the given user name and granted authorities
     */
    public Authentication getAuthentication (String userName,
                                             List<GrantedAuthority> authorities,
                                             HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken =
                new UsernamePasswordAuthenticationToken(
                        userName,
                        null,
                        authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    /**
     * Returns a boolean indicating whether the given JWT token is valid for the given user name.
     *
     * @param userName the username
     * @param token the JWT token
     * @return a boolean indicating whether the given JWT token is valid for the given user name
     */
    public Boolean isTokenValid(String userName, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotBlank(userName) && !isTokenExpired(verifier, token);
    }

    /**
     * Returns the subject extracted from the given JWT token.
     *
     * @param token the JWT token
     * @return the subject extracted from the given JWT token
     */
    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

}
