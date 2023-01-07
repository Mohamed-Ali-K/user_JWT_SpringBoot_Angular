package com.kenis.supportportal.service.impl;

import com.kenis.supportportal.domain.User;
import com.kenis.supportportal.domain.UserPrincipal;
import com.kenis.supportportal.exception.domain.EmailExistException;
import com.kenis.supportportal.exception.domain.UserNameExistException;
import com.kenis.supportportal.exception.domain.UserNotFoundException;
import com.kenis.supportportal.repository.UserRepository;
import com.kenis.supportportal.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;


import static com.kenis.supportportal.constant.UserImpConstant.*;
import static com.kenis.supportportal.enumeration.Role.*;

/**
 * Implementation of the {@link UserService} and {@link UserDetailsService} interfaces.
 *
 * <p>This class provides methods for managing users and for authenticating users using their user names.
 * It uses the {@link UserRepository} for storing and retrieving users from the database and the
 * {@link BCryptPasswordEncoder} for encoding user passwords.
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

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads the user with the specified user name.
     *
     * <p>This method is used to load the user with the specified user name. It takes a single parameter,
     * {@code username}, which is the user name of the user to load. The method first tries to find the
     * user with the specified user name using the {@link UserRepository#findUserByUsername(String)}
     * method. If a user with the specified user name is not found, it throws a
     * {@link UsernameNotFoundException} with an error message. If a user with the specified user name is
     * found, it updates the user's last login date and saves the updated user to the database using the
     * {@link UserRepository#save(User)} method. It then creates a new {@link UserPrincipal} object using
     * the found user and returns it.
     *
     * @param username the user name of the user to load
     * @return the user details
     * @throws UsernameNotFoundException if the user with the specified user name does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME  + username);
        } else {
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
     * @param userName  the user name of the user
     * @param email     the email address of the user
     * @return the registered user
     * @throws UserNotFoundException  if the user does not exist
     * @throws EmailExistException    if the email address is already in use
     * @throws UserNameExistException if the user name is already in use
     */
    @Override
    public User register(String firstName, String lastName, String userName, String email)
            throws UserNotFoundException, EmailExistException, UserNameExistException {
        validateNewUserNameAndEmail(StringUtils.EMPTY, userName, email);
        String password = generatePassword();
        String userId = generateUserId();
        String encodedPassword = encodePassword(password);
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(userName);
        user.setEmail(email);
        user.setUserId(userId);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        userRepository.save(user);
        log.info("New user password : " + password);

        return user;
    }

    /**
     * Generates a unique user ID that does not exist in the database.
     *
     * @return the generated unique user ID
     */
    private String generateUserId() {
        String userId = RandomStringUtils.randomNumeric(10);
        User currentUser = userRepository.findUserByUserId(userId);
        while (currentUser != null) {
            userId = RandomStringUtils.randomNumeric(10);
            currentUser = userRepository.findUserByUserId(userId);
        }
        return userId;
    }

    /**
     * Returns the URL of the temporary profile image.
     *
     * @return the URL of the temporary profile image
     */
    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PROFILE_TEMP_PATH).toUriString();
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
     * @param currentUserName the current username of the user (may be blank)
     * @param newUserName     the new username to validate
     * @param newEmail        the new email address to validate
     * @return the current user, if the current username is not blank
     * @throws UserNotFoundException  if the current username is not blank and no such user exists
     * @throws UserNameExistException if the new username is already in use
     * @throws EmailExistException    if the new email address is already in use
     */
    private User validateNewUserNameAndEmail(String currentUserName, String newUserName, String newEmail)
            throws UserNotFoundException, UserNameExistException, EmailExistException {
        User userByNewUserName = findUserByUserName(newUserName);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUserName)) {
            User currentUser = findUserByUserName(currentUserName);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUserName);
            }

            if (userByNewUserName != null && currentUser.getId().equals(userByNewUserName.getId())) {
                throw new UserNameExistException(USERNAME_ALREADY_EXISTS);
            }

            if (userByNewEmail != null && currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {

            if (userByNewUserName != null) {
                throw new UserNameExistException(USERNAME_ALREADY_EXISTS);
            }

            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
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
     * @param userName the user's username
     * @return the user, or null if no such user exists
     */
    @Override
    public User findUserByUserName(String userName) {
        return userRepository.findUserByUsername(userName);
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
}
