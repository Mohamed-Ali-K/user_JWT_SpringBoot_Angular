package com.kenis.supportportal.service.impl;

import com.kenis.supportportal.domain.User;
import com.kenis.supportportal.domain.UserPrincipal;
import com.kenis.supportportal.enumeration.Role;
import com.kenis.supportportal.exception.domain.*;
import com.kenis.supportportal.repository.UserRepository;
import com.kenis.supportportal.service.EmailService;
import com.kenis.supportportal.service.LoginAttemptService;
import com.kenis.supportportal.service.UserService;
import com.kenis.supportportal.utility.FieldsValidations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kenis.supportportal.utility.FieldsValidations.Field;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Date;
import java.util.List;


import static com.kenis.supportportal.constant.FileConstant.*;
import static com.kenis.supportportal.constant.UserImpConstant.*;
import static com.kenis.supportportal.enumeration.Role.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Implementation of the {@link UserService} and {@link UserDetailsService} interfaces.
 *
 * <p>This class provides methods for managing users and for authenticating users using their usernames.
 * It uses the {@link UserRepository#save(Object)} for storing and retrieving users from the database and the
 * {@link BCryptPasswordEncoder} for encoding user passwords. It also uses the {@link LoginAttemptService}
 * to check whether a user has exceeded the maximum number of login attempts. It also uses the {@link EmailService}
 * to send emails.
 *
 * @author Mohamed Ali Kenis
 */
@Slf4j
@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final LoginAttemptService loginAttemptService ;

    private final EmailService emailService;

    private final FieldsValidations validations;

    /**
     * Constructs a new {@code UserServiceImpl} object with the given dependencies.
     *
     * @param userRepository      the repository for storing and retrieving users from the database
     * @param passwordEncoder     the password encoder for encoding user passwords
     * @param loginAttemptService the service for checking login attempts
     * @param emailService        the service for sending emails
     * @param validations          the service for validate blank or empty fields
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, EmailService emailService, FieldsValidations validations) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.validations = validations;
    }

    /**
     * Loads the user with the specified username.
     *
     * <p>This method is used to load the user with the specified username. It takes a single parameter,
     * {@code username}, which is the username of the user to load. The method first tries to find the
     * user with the specified username using the {@link UserRepository#findUserByUsername(String)}
     * method. If a user with the specified username is not found, it throws a
     * {@link UsernameNotFoundException} with an error message. If a user with the specified username is
     * found, it updates the user's last login date and saves the updated user to the database using the
     * {@link UserRepository#save(Object)} method. It then creates a new {@link UserPrincipal} object using
     * the found user and returns it.
     *
     * @param username the username of the user to load
     * @return the user details
     * @throws UsernameNotFoundException if the user with the specified username does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME  + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDate(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(RETURNING_FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }

    }


    /**
     * Registers a new user with the given information.
     * This method checks that the given username and email address are not already in use,
     * and throws an exception if they are.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param username  the username of the user
     * @param email     the email address of the user
     * @return the registered user
     * @throws UserNotFoundException  if the user does not exist
     * @throws EmailExistException    if the email address is already in use
     * @throws UsernameExistException if the username is already in use
     * @throws MessagingException     if there was a problem sending the email with the new password
     */
    @Override
    public User register(String firstName, String lastName, String username, String email)
            throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUserNameAndEmail(EMPTY, username, email);
        String password = generatePassword();
        String userId = generateUserId();
        String encodedPassword = encodePassword(password);
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setUserId(userId);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(firstName,password,email);
        log.info("New user password : " + password);

        return user;
    }

    /**
     * Adds a new user with the given information.
     *
     * <p>This method generates a temporary password and user ID for the new user, and encodes the password using
     * the {@link #encodePassword(String)} method. It then creates a new {@link User} object with the given
     * information and the generated password and user ID, and sets the user's role and authorities based on the
     * given role. It also sets the user's profile image URL to a temporary URL and saves the user to the
     * database using the {@link UserRepository save(User)} method. Finally, it saves the given profile image
     * to the server using the {@link #saveProfileImage(User, MultipartFile)} method.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param username the user's username
     * @param email the user's email address
     * @param role the user's role
     * @param isNotLocked whether the user is locked or not
     * @param isActive whether the user is active or not
     * @param profileImage the user's profile image
     * @return the added user
     * @throws UserNotFoundException if the user does not exist
     * @throws EmailExistException if the email address is already in use
     * @throws UsernameExistException if the username is already in use
     * @throws IOException if there is an error saving the profile image
     */
    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, BlankFieldException {
        validateUserFields(firstName,lastName,username,email,role,isNotLocked,isActive);
        validateNewUserNameAndEmail(EMPTY, username,email);
        String password = generatePassword();
        String userId = generateUserId();
        String encodedPassword = encodePassword(password);
        User user = new User();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setIsNotLocked(isNotLocked);
        user.setIsActive(isActive);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }


    /**
     * Updates the information for the user with the given username.
     *
     * <p>This method first retrieves & validate the user with the given username using the
     * {@link #validateNewUserNameAndEmail(String, String, String)} method, which also checks that the
     * new username and email address are not already in use. It then updates the user's first name, last name,
     * username, email address, locked status, active status, role, and authorities with the given values, and
     * saves the updated user to the database using the {@link UserRepository save(User)} method. It also saves
     * the given profile image to the server using the {@link #saveProfileImage(User, MultipartFile)} method.
     *
     * @param currentUsername the current username of the user to be updated
     * @param newFirstName the new first name of the user
     * @param newLastName the new last name of the user
     * @param newUsername the new username of the user
     * @param newEmail the new email address of the user
     * @param role the new role of the user
     * @param isNotLocked whether the user is locked or not
     * @param isActive whether the user is active or not
     * @param profileImage the new profile image of the user
     * @return the updated user
     * @throws UserNotFoundException if the user does not exist
     * @throws EmailExistException if the new email address is already in use
     * @throws UsernameExistException if the new username is already in use
     * @throws IOException if there is an error saving the profile image
     */
    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        User currentUser = validateNewUserNameAndEmail(currentUsername, newUsername,newEmail);

        assert currentUser != null;
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setIsNotLocked(isNotLocked);
        currentUser.setIsActive(isActive);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    /**
     * Deletes the user with the given ID.
     *
     * @param id the ID of the user to delete
     */
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);

    }

    /**
     * Resets the password for the user with the given email address.
     *
     * <p>This method retrieves the user with the given email address using the
     * {@link UserRepository findUserByEmail(String)} method, and generates a new temporary password for the
     * user. It then encodes the new password using the {@link #encodePassword(String)} method, updates the
     * user's password in the database using the {@link UserRepository save(User)} method, and sends an email
     * with the new password to the user's email address using the {@link EmailService#sendNewPasswordEmail(String, String, String)}
     * method. If no user with the given email address is found, it throws an {@link EmailNotFoundException}
     * with an error message.
     *
     * @param email the email address of the user to reset the password for
     * @throws MessagingException if there is an error sending the email
     * @throws EmailNotFoundException if no user with the given email address is found
     */
    @Override
    public void resetPassword(String email) throws MessagingException, EmailNotFoundException {

        User user = userRepository.findUserByEmail(email);
        if (user==null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL+ email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(),password,email);
    }

    /**
     * Updates the profile image for the user with the given username.
     *
     * <p>This method first retrieves & validate the user with the given username using the
     * {@link #validateNewUserNameAndEmail(String, String, String)} method. It then saves the given profile
     * image to the server using the {@link #saveProfileImage(User, MultipartFile)} method.
     *
     * @param username the username of the user to update the profile image for
     * @param newProfileImage the new profile image for the user
     * @return the updated user
     * @throws UserNotFoundException if the user does not exist
     * @throws EmailExistException if the email address is already in use
     * @throws UsernameExistException if the username is already in use
     * @throws IOException if there is an error saving the profile image
     */
    @Override
    public User updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        User user = validateNewUserNameAndEmail(username,null,null);
        saveProfileImage(user,newProfileImage);
        return user;
    }


    /**
     * This method retrieves a list of all registered users from the database.
     *
     * @return a list of users
     */
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * This method finds a user by their username.
     *
     * @param username the user's username
     * @return the user, or null if no such user exists
     */
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * This method finds a user by their email address.
     *
     * @param email the user's email address
     * @return the user, or null if no such user exists
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    @Override
    public User getUser( String identifier) throws UserNotFoundException, BlankFieldException {
        validations.validationField("identifier", identifier);
        User user = null;
        try {
            Long id = Long.parseLong(identifier);
            user = userRepository.findUserById(id);
        } catch (NumberFormatException e) {
            if (identifier.startsWith("ID_")) {
                user = userRepository.findUserByUserId(identifier);
            }else if (identifier.contains("@")) {
                user = userRepository.findUserByEmail(identifier);
            }else if(identifier.matches("^[a-zA-Z0-9._-]+$")) {
                user = userRepository.findUserByUsername(identifier);
            }
        }
        if (user == null) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_IDENTIFIER + identifier);
        }
        return user;
    }

    /**
     * Generates a unique user ID that does not exist in the database.
     *
     * @return the generated unique user ID
     */
    private String generateUserId() {
        String  preFix = "ID_";
        String userId = RandomStringUtils.randomNumeric(10);
        User currentUser = userRepository.findUserByUserId(userId);
        while (currentUser != null) {
            userId = RandomStringUtils.randomNumeric(10);
            currentUser = userRepository.findUserByUserId(userId);
        }
        userId = preFix + userId;
        return userId;
    }

    /**
     * Returns the URL of the temporary profile image.
     *
     * @return the URL of the temporary profile image
     */
    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    /**
     * Encodes the specified password.
     *
     * @param password the password to encode
     * @return the encoded password
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Generates a random alphabetic password.
     *
     * @return the generated random alphabetic password
     */
    private String generatePassword() {
        return RandomStringUtils.randomAlphabetic(10);
    }


    /**
     * Validates a new username and email address for a user.
     * If the current username is not blank, this method checks that the new username and email address
     * are not already in use by another user, and throws an exception if they are.
     * If the current username is blank, this method checks that the new username and email address are not
     * already in use by any user, and throws an exception if they are.
     *
     * @param currentUsername the current username of the user (maybe blank)
     * @param newUsername     the new username to validate
     * @param newEmail        the new email address to validate
     * @return the current user, if the current username is not blank
     * @throws UserNotFoundException  if the current username is not blank and no such user exists
     * @throws UsernameExistException if the new username is already in use
     * @throws EmailExistException    if the new email address is already in use
     */
    private User validateNewUserNameAndEmail(String currentUsername, String newUsername, String newEmail)
            throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if (isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }

            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }

            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {

            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }

            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    /**
     * Validates the login attempt for the given user.
     *
     * <p>This method is used to validate the login attempt for the given user. If the user is not locked,
     * it checks whether the user has exceeded the maximum number of login attempts using the
     * {@link LoginAttemptService#hasExceededMaxAttempts(String)} method. If the user has exceeded the maximum
     * number of login attempts, it sets the user's {@code isNotLocked} field to {@code false}. If the user has not
     * exceeded the maximum number of login attempts, it sets the user's {@code isNotLocked} field to {@code true}.
     * If the user is locked, it removes the user from the login attempt cache using the
     * {@link LoginAttemptService#evictUserFromLoginAttemptCache(String)} method.
     *
     * @param user the user to validate the login attempt for
     */
    private void validateLoginAttempt(User user) {
        if(user.getIsNotLocked()) {
            user.setIsNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    /**
     * Saves the given profile image for the given user to the file system.
     *
     * <p>This method first checks if the given profile image is not null. If it is not, it creates a new
     * directory on the file system for the user if it does not already exist, using the {@link Files#createDirectories(Path, FileAttribute[])}
     * method. It then deletes any existing profile image for the user using the {@link Files#deleteIfExists(Path)}
     * method and saves the new profile image to the file system using the {@link Files#copy(InputStream, Path, CopyOption...)}
     * method. It then updates the user's profile image URL in the database using the {@link UserRepository#save(Object)}
     * method.
     *
     * @param user the user to save the profile image for
     * @param profileImage the profile image to save
     * @throws IOException if there is an error saving the profile image
     */
    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(),userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    /**
     * Returns a URL for the profile image of the user with the given username.
     *
     * @param username the username of the user
     * @return a URL for the user's profile image
     */
    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
    }

    /**
     * Returns the role enum value with the specified name. The case of the input string is ignored.
     *
     * @param role the name of the role, as a string
     * @return the role enum value with the specified name
     * @throws IllegalArgumentException if the input string does not match the name of the enum values
     */
    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private void validateUserFields(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive) throws BlankFieldException {
        validations.validateFields(List.of(
                new Field("First Name", firstName),
                new Field("Last Name", lastName),
                new Field("Username", username),
                new Field("Email", email),
                new Field("Role", role),
                new Field("isNotLocked", String.valueOf(isNotLocked)),
                new Field("isActive", String.valueOf(isActive))
        ));
    }

}
