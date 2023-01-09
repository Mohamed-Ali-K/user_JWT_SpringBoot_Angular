package com.kenis.supportportal.resource;




import com.kenis.supportportal.domain.User;
import com.kenis.supportportal.domain.UserPrincipal;
import com.kenis.supportportal.exception.domain.EmailExistException;
import com.kenis.supportportal.exception.domain.ExceptionHandling;
import com.kenis.supportportal.exception.domain.UsernameExistException;
import com.kenis.supportportal.exception.domain.UserNotFoundException;
import com.kenis.supportportal.service.UserService;
import com.kenis.supportportal.utility.JWTTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static com.kenis.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;

/**
 * The UserResource class is a REST controller that handles HTTP requests related to user
 * registration, login, and other user-related operations.
 *
 * @author Kenis
 */
@Slf4j
@RestController
@RequestMapping(path = {"/","/user"})
public class UserResource extends ExceptionHandling {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    /**
     * Constructs a new UserResource instance with the given user service,
     * authentication manager, and JWT token provider.
     *
     * @param userService the user service
     * @param authenticationManager the authentication manager
     * @param jwtTokenProvider the JWT token provider
     */
    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Handles a login request and returns the user's information and a JWT token in the response header.
     *
     * @param user the user's login credentials
     * @return the user's information and a JWT token in the response header
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeaders, HttpStatus.OK);
    }

    /**
     * Handles a user registration request and returns the registered user's information.
     *
     * @param user the user's registration information
     * @return the registered user's information
     * @throws UserNotFoundException if the user cannot be found
     * @throws EmailExistException if the email address is already registered
     * @throws UsernameExistException if the username is already taken
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        log.info("user :{}", newUser);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    //= Private Methods ==
    /**
     * Returns an HttpHeaders object with a JWT token in the "Authorization" header.
     *
     * @param userPrincipal the user principal
     * @return an HttpHeaders object with a JWT token in the "Authorization" header
     */
    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }
    /**
     * Authenticates the user with the given username and password.
     *
     * @param userName the username
     * @param password the password
     */
    private void authenticate(String userName, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName,password));
    }
}
