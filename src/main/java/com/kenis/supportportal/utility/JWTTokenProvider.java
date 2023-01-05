package com.kenis.supportportal.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import static com.kenis.supportportal.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.kenis.supportportal.domain.UserPrincipal;
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

@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    //= Private Methods ==
    private String[]  getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier
                .verify(token)
                .getClaim(AUTHORITIES)
                .asArray(String.class);
    }

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

    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    //= Public Methods ==

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

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

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

    public Boolean isTokenValid(String userName, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNotBlank(userName) && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

}
