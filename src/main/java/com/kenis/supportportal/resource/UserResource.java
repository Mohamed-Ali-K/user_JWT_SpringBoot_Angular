package com.kenis.supportportal.resource;


import com.kenis.supportportal.domain.HttpResponse;
import com.kenis.supportportal.domain.User;
import com.kenis.supportportal.domain.UserPrincipal;
import com.kenis.supportportal.exception.domain.*;
import com.kenis.supportportal.service.UserService;
import com.kenis.supportportal.utility.JWTTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.kenis.supportportal.constant.FileConstant.*;
import static com.kenis.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

/**
 * The UserResource class is a REST controller that handles HTTP requests related to user
 * registration, login, and other user-related operations.
 *
 * @author Kenis
 */
@Slf4j
@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {
    public static final String PASSWORD_EMAIL_SEND_TO = "An email with new password was send to ";
    public static final String DELETED_SUCCESSFULLY_USER_ID = "The user was deleted successfully, user id: ";
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    /**
     * Constructs a new UserResource instance with the given user service,
     * authentication manager, and JWT token provider.
     *
     * @param userService           the user service
     * @param authenticationManager the authentication manager
     * @param jwtTokenProvider      the JWT token provider
     */
    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager,
                        JWTTokenProvider jwtTokenProvider) {
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
        return new ResponseEntity<>(loginUser, jwtHeaders, OK);
    }

    /**
     * Handles a user registration request and returns the registered user's information.
     *
     * @param user the user's registration information
     * @return the registered user's information
     * @throws UserNotFoundException  if the user cannot be found
     * @throws EmailExistException    if the email address is already registered
     * @throws UsernameExistException if the username is already taken
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException,
            UsernameExistException, MessagingException, IOException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail());
        log.info("user :{}", newUser);
        return new ResponseEntity<>(newUser, OK);
    }
    /**
     * Handles a request to add a new user.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param username the user's username
     * @param email the user's email
     * @param role the user's role
     * @param isNotLock whether the user is locked
     * @param isActive whether the user is active
     * @param profileImage the user's profile image (optional)
     * @return the added user's information and a status of OK
     * @throws UserNotFoundException if the user cannot be found
     * @throws EmailExistException if the email address is already in use
     * @throws IOException if an error occurs while reading or writing the user's profile image
     * @throws UsernameExistException if the username is already in use
     */
    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNotLock") String isNotLock,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, BlankFieldException {
        User user = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNotLock), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(user, OK);
    }

    /**
     * Handles a request to update a user's information.
     *
     * @param currentUsername the current username of the user to update
     * @param newFirstName the new first name for the user
     * @param newLastName the new last name for the user
     * @param newUsername the new username for the user
     * @param newEmail the new email for the user
     * @param role the new role for the user
     * @param isNotLock whether the user is locked
     * @param isActive whether the user is active
     * @param profileImage the new profile image for the user (optional)
     * @return the updated user's information and a status of OK
     * @throws UserNotFoundException if the user to update cannot be found
     * @throws EmailExistException if the new email address is already in use
     * @throws IOException if an error occurs while reading or writing the user's profile image
     * @throws UsernameExistException if the new username is already in use
     */
    @PostMapping("/update")
    public ResponseEntity<User> updateUser(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("newFirstName") String newFirstName,
            @RequestParam("newLastName") String newLastName,
            @RequestParam("newUsername") String newUsername,
            @RequestParam("newEmail") String newEmail,
            @RequestParam("role") String role,
            @RequestParam("isNotLock") String isNotLock,
            @RequestParam("isActive") String isActive,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User updatedUser = userService.updateUser(
                currentUsername, newFirstName, newLastName, newUsername, newEmail, role,
                Boolean.parseBoolean(isNotLock), Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    /**
     * Handles a request to retrieve a list of all users.
     *
     * @return a list of all users and a status of OK
     */
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users,OK);
    }

    /**
     * Handles a request to reset a user's password.
     *
     * @param email the email of the user to reset the password for
     * @return a response with a status of OK and a message indicating that an email with the new password was sent
     * @throws EmailNotFoundException if the email address is not associated with any user
     * @throws MessagingException if there is an error while sending the password reset email
     */
    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, PASSWORD_EMAIL_SEND_TO + email);
    }

    /**
     * Handles a request to delete a user.
     *<br>
     *the preAuthorize the user making the request must have the 'user:delete' authority
     * @param id the id of the user to delete
     * @return a response with a status of NO_CONTENT and a message indicating that the user was deleted successfully
     *
     */
    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deletedUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return response(NO_CONTENT, DELETED_SUCCESSFULLY_USER_ID + id);
    }

    /**
     * Handles a request to retrieve a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the user with the given username and a status of OK
     */
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws UserNotFoundException, BlankFieldException {
        User user = userService.getUser(username);
        return new ResponseEntity<>(user,OK);
    }

    /**
     * Handles a request to retrieve a user's profile image by their username and file name.
     *
     * @param username the username of the user whose profile image to retrieve
     * @param fileName the file name of the profile image to retrieve
     * @return the user's profile image as a byte array
     * @throws IOException if there is an error reading the file
     */
    @GetMapping(path =  "/image/{username}/{fileName}",produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(
            @PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    /**
     * Handles a request to retrieve a user's temporary profile image by their username.
     * This method first creates a URL using the given username and a base URL defined in the
     * {@code TEMP_PROFILE_IMAGE_BASE_URL} constant. It then reads the image from the URL and returns it
     * as a byte array.
     *
     * @param username the username of the user whose temporary profile image to retrieve
     * @return the user's temporary profile image as a byte array
     * @throws IOException if there is an error reading the image from the URL
     */
    @GetMapping(path =  "/image/profile/{username}",produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(
           @PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk,0,bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Handles a request to update the profile image of a user.
     *
     * @param username the username of the user whose profile image is being updated
     * @param newProfileImage the new profile image for the user
     * @return the updated user information
     * @throws UserNotFoundException if the user with the given username cannot be found
     * @throws IOException if there is an error reading or writing the profile image file
     */
    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(
            @RequestParam("username") String username,
            @RequestParam(value = " newProfileImage") MultipartFile  newProfileImage
    ) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User user = userService.updateProfileImage(username, newProfileImage);
        return new ResponseEntity<>(user, OK);
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
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    /**
     * Authenticates the user with the given username and password.
     *
     * @param userName the username
     * @param password the password
     */
    private void authenticate(String userName, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, password));
    }

    /**
     * Creates and returns a {@link ResponseEntity} object with a {@link HttpResponse} object as the body
     * and the given {@code httpStatus} as the status. The {@link HttpResponse} object has the given
     * {@code message} as well as the {@code value} and {@code reasonPhrase} of the {@code httpStatus}
     * set as its fields.
     *
     * @param httpStatus the status of the response
     * @param message the message to include in the response body
     * @return a {@link ResponseEntity} object with a {@link HttpResponse} object as the body
     *         and the given {@code httpStatus} as the status
     */
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse httpResponse = new HttpResponse(
                httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(
                httpResponse,
                httpStatus
        );
    }
}
