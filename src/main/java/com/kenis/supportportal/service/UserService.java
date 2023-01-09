package com.kenis.supportportal.service;

import com.kenis.supportportal.domain.User;
import com.kenis.supportportal.exception.domain.EmailExistException;
import com.kenis.supportportal.exception.domain.EmailNotFoundException;
import com.kenis.supportportal.exception.domain.UsernameExistException;
import com.kenis.supportportal.exception.domain.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * This is an interface for a service that manages users.
 * It provides methods for registering users, retrieving a list of users,
 * adding new users, updating existing users, deleting users, resetting passwords,
 * and finding a user by username or email.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
public interface UserService {
    /**
     * Registers a new user with the given information.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param username the user's username
     * @param email the user's email address
     * @return the registered user
     * @throws UserNotFoundException if the user cannot be found
     * @throws EmailExistException if the email already exists
     * @throws UsernameExistException if the username already exists
     * @throws MessagingException if there is an error sending an email
     */
    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException, IOException;


    /**
     * Returns a list of all registered users.
     *
     * @return a list of users
     */
    List<User> getUsers();


    /**
     * Finds a user by their username.
     *
     * @param username the user's username
     * @return the user, or null if no such user exists
     */
    User findUserByUsername(String username);


    /**
     * Finds a user by their email address.
     *
     * @param email the user's email address
     * @return the user, or null if no such user exists
     */
    User findUserByEmail(String email);

    /**
     * Adds a new user with the given information.
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
     */
    User addNewUser(
            String firstName,
            String lastName,
            String username,
            String email,
            String role,
            boolean isNotLocked,
            boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

/**
 * Updates the information for the user with the given username.
 *
 * @param currentUsername the current username of the user
 * @param newFirstName the new first name for the user
 * @param newLastName the new last name for the user
 * @param newUsername the new username for the user
 * @param newEmail the new email for the user
 * @param role the new role for the user
 * @param isNotLocked whether the user should be locked or not
 * @param isActive whether the user should be active or not
 * @param profileImage the new profile image for the user
 * @return the updated user
 */
    User updateUser(
            String currentUsername,
            String newFirstName,
            String newLastName,
            String newUsername,
            String newEmail,
            String role,
            boolean isNotLocked,
            boolean isActive,
            MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;

    /**
     * Deletes the user with the given ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(Long id);

    /**
     * Resets the password for the user with the given email address.
     *
     * @param email the email address of the user
     */
    void resetPassword(String email) throws MessagingException, EmailNotFoundException;

    /**
     * Updates the profile image for the user with the given username.
     *
     * @param username the username of the user
     * @param newProfileImage the new profile image for the user
     * @return the updated user
     */
    User updateProfileImage(String username, MultipartFile newProfileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException;
}





