package com.kenis.usermanager.resource;


import com.kenis.usermanager.domain.HttpResponse;
import com.kenis.usermanager.domain.User;
import com.kenis.usermanager.domain.UserPrincipal;
import com.kenis.usermanager.exception.domain.*;
import com.kenis.usermanager.service.UserService;
import com.kenis.usermanager.utility.FieldsValidations;
import com.kenis.usermanager.utility.JWTTokenProvider;
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

import static com.kenis.usermanager.constant.FileConstant.*;
import static com.kenis.usermanager.constant.SecurityConstant.JWT_TOKEN_HEADER;
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
    private final FieldsValidations validations;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    /**
     * Constructs a new UserResource instance with the given user service,
     * authentication manager, and JWT token provider.
     *
     * @param userService           The service for user-related operations.
     * @param validations           The service for field validations.
     * @param authenticationManager The Spring security authentication manager.
     * @param jwtTokenProvider      The utility for JWT token operations.
     */
    @Autowired
    public UserResource(UserService userService, FieldsValidations validations, AuthenticationManager authenticationManager,
                        JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.validations = validations;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Handles a login request, authenticates the user, and returns the user's information along with a JWT token in the response header.
     *
     * @param user The {@code User} object containing login credentials (username and password).
     * @return A {@code ResponseEntity} containing the authenticated {@code User} and JWT token in headers.
     * @throws BlankFieldException if username or password fields are blank.
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) throws BlankFieldException {
        validations.validateFieldsLogin(user.getUsername(),user.getPassword());
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeaders, OK);
    }

    /**
     * Handles a user registration request.
     *
     * @param user The {@code User} object containing registration information (firstName, lastName, username, email).
     * @return A {@code ResponseEntity} containing the newly registered {@code User} and HTTP status {@code OK}.
     * @throws UserNotFoundException  If an issue arises with finding user-related data during registration (should be rare for new registration).
     * @throws EmailExistException    If the provided email address is already registered.
     * @throws UsernameExistException If the provided username is already taken.
     * @throws MessagingException     If there is an error sending the registration confirmation email.
     * @throws IOException            If an I/O error occurs (e.g. profile image handling if added to this method).
     * @throws BlankFieldException    If any required registration fields are blank.
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException,
            UsernameExistException, MessagingException, IOException, BlankFieldException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail());
        log.info("user :{}", newUser);
        return new ResponseEntity<>(newUser, OK);
    }

    /**
     * Handles the HTTP POST request to add a new user.
     *
     * @param firstName The first name of the new user.
     * @param lastName The last name of the new user.
     * @param username The username for the new user.
     * @param email The email address for the new user.
     * @param role The role assigned to the new user.
     * @param isActive String representation of whether the user is active ("true" or "false").
     * @param isNotLock String representation of whether the user is not locked ("true" or "false").
     * @param profileImage (Optional) The profile image file for the new user.
     * @return A {@code ResponseEntity} containing the newly created {@code User} and HTTP status {@code OK}.
     * @throws UserNotFoundException If an error occurs related to user not being found during underlying processes (though typically not expected for a new user).
     * @throws EmailExistException If the provided email already exists.
     * @throws IOException If an error occurs during profile image processing.
     * @throws UsernameExistException If the provided username already exists.
     * @throws BlankFieldException If any required field is blank.
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
     * Handles the HTTP POST request to update an existing user's information.
     *
     * @param currentUsername The current username of the user to be updated.
     * @param newFirstName The new first name for the user.
     * @param newLastName The new last name for the user.
     * @param newUsername The new username for the user.
     * @param newEmail The new email address for the user.
     * @param role The new role for the user.
     * @param isNotLock String representation of whether the user should be not locked ("true" or "false").
     * @param isActive String representation of whether the user should be active ("true" or "false").
     * @param profileImage (Optional) The new profile image file for the user.
     * @return A {@code ResponseEntity} containing the updated {@code User} and HTTP status {@code OK}.
     * @throws UserNotFoundException If the user with {@code currentUsername} is not found.
     * @throws EmailExistException If the {@code newEmail} already exists for another user.
     * @throws IOException If an error occurs during profile image processing.
     * @throws UsernameExistException If the {@code newUsername} already exists for another user.
     * @throws BlankFieldException If any required field for the update is blank.
     */
    @PostMapping("/update")
    public ResponseEntity<User> updateUser(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String newFirstName,
            @RequestParam("lastName") String newLastName,
            @RequestParam("username") String newUsername,
            @RequestParam("email") String newEmail,
            @RequestParam("role") String role,
            @RequestParam("isNotLock") String isNotLock,
            @RequestParam("isActive") String isActive,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, BlankFieldException {
        User updatedUser = userService.updateUser(
                currentUsername, newFirstName, newLastName, newUsername, newEmail, role,
                Boolean.parseBoolean(isNotLock), Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    /**
     * Retrieves a list of all users.
     *
     * @return A {@code ResponseEntity} containing a list of all {@code User} objects and HTTP status {@code OK}.
     */
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users,OK);
    }

    /**
     * Initiates a password reset process for the user with the given email address.
     *
     * @param email The email address of the user whose password needs to be reset.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code OK} and a success message.
     * @throws EmailNotFoundException If the email address is not associated with any user.
     * @throws MessagingException If there is an error while sending the password reset email.
     * @throws BlankFieldException If the provided email is blank.
     */
    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws EmailNotFoundException, MessagingException, BlankFieldException {
        userService.resetPassword(email);
        return response(OK, PASSWORD_EMAIL_SEND_TO + email);
    }

    /**
     * Deletes a user by their ID. Access is restricted to users with 'user:delete' authority.
     *
     * @param id The ID of the user to delete.
     * @return A {@code ResponseEntity<HttpResponse>} with status {@code NO_CONTENT} and a success message.
     */
    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deletedUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return response(NO_CONTENT, DELETED_SUCCESSFULLY_USER_ID + id);
    }

    /**
     * Retrieves a user by their username (or other identifier as processed by {@code userService.getUser}).
     *
     * @param username The username or identifier of the user to retrieve.
     * @return A {@code ResponseEntity} containing the found {@code User} and HTTP status {@code OK}.
     * @throws UserNotFoundException if the user is not found.
     * @throws BlankFieldException if the provided username is blank.
     */
    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws UserNotFoundException, BlankFieldException {
        User user = userService.getUser(username);
        return new ResponseEntity<>(user,OK);
    }

    /**
     * Handles a request to retrieve a user's profile image by their username and file name.
     *
     * @param username The username of the user whose profile image is to be retrieved.
     * @param fileName The name of the image file.
     * @return A byte array containing the profile image data.
     * @throws IOException If an error occurs while reading the image file.
     */
    @GetMapping(path =  "/image/{username}/{fileName}",produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(
            @PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    /**
     * Retrieves a temporary profile image for a user, typically from an external service (e.g., Robohash).
     *
     * @param username The username for whom to fetch the temporary profile image.
     * @return A byte array containing the temporary profile image data.
     * @throws IOException If an error occurs while fetching or reading the image from the URL.
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
     * Handles the HTTP POST request to update a user's profile image.
     *
     * @param username The username of the user whose profile image is to be updated.
     * @param newProfileImage The new profile image file.
     * @return A {@code ResponseEntity} containing the updated {@code User} (with the new profile image URL) and HTTP status {@code OK}.
     * @throws UserNotFoundException If the user with {@code username} is not found.
     * @throws EmailExistException If an email-related conflict occurs during the update (should be reviewed if only image is updated).
     * @throws IOException If an error occurs during profile image processing.
     * @throws UsernameExistException If a username-related conflict occurs during the update (should be reviewed if only image is updated).
     */
    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(
            @RequestParam("username") String username,
            @RequestParam(value = " newProfileImage") MultipartFile  newProfileImage
    ) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException { // Added missing BlankFieldException if username could be blank
        User user = userService.updateProfileImage(username, newProfileImage);
        return new ResponseEntity<>(user, OK);
    }

    //= Private Methods ==

    /**
     * Generates HTTP headers containing a JWT token for the given user principal.
     *
     * @param userPrincipal The user principal for whom to generate the token.
     * @return {@code HttpHeaders} object with the JWT token added to the {@code JWT_TOKEN_HEADER}.
     */
    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    /**
     * Authenticates a user using the Spring {@code AuthenticationManager}.
     *
     * @param userName The username to authenticate.
     * @param password The password for the given username.
     * @throws org.springframework.security.core.AuthenticationException if authentication fails (e.g., {@code BadCredentialsException}).
     */
    private void authenticate(String userName, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, password));
    }

    /**
     * Creates a standardized {@code ResponseEntity<HttpResponse>} object.
     * This is a utility method for generating consistent API responses.
     *
     * @param httpStatus The {@code HttpStatus} for the response.
     * @param message The message to include in the {@code HttpResponse} body.
     * @return A {@code ResponseEntity} configured with the given status and message.
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
