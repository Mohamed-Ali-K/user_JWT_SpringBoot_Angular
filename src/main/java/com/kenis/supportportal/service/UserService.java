package com.kenis.supportportal.service;

import com.kenis.supportportal.domain.User;
import com.kenis.supportportal.exception.domain.EmailExistException;
import com.kenis.supportportal.exception.domain.UserNameExistException;
import com.kenis.supportportal.exception.domain.UserNotFoundException;

import javax.mail.MessagingException;
import java.util.List;

/**
 * This is an interface for a service that manages users.
 * It provides methods for registering users, retrieving a list of users,
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
     * @param userName the user's username
     * @param email the user's email address
     * @return the registered user
     */
    User register(String firstName, String lastName, String userName, String email) throws UserNotFoundException, EmailExistException, UserNameExistException, MessagingException;


    /**
     * Returns a list of all registered users.
     *
     * @return a list of users
     */
    List<User> getUsers();


    /**
     * Finds a user by their username.
     *
     * @param userName the user's username
     * @return the user, or null if no such user exists
     */
    User findUserByUserName(String userName);


    /**
     * Finds a user by their email address.
     *
     * @param email the user's email address
     * @return the user, or null if no such user exists
     */
    User findUserByEmail(String email);
}





