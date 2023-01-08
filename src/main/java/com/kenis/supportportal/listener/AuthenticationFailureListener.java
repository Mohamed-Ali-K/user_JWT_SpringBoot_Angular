package com.kenis.supportportal.listener;


import com.kenis.supportportal.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
/**
 * <h3> Listener that listens for authentication failure events and updates the login attempts for the corresponding user.
 *
 * <p>The {@code AuthenticationFailureListener} class has the following constructors:
 *
 * <ul>
 *     <li>{@link #AuthenticationFailureListener(LoginAttemptService)} constructs a new {@code AuthenticationFailureListener} instance and
 *     initializes the {@code loginAttemptService} field with the given {@link LoginAttemptService} instance.</li>
 * </ul>
 *
 * <p> The {@code AuthenticationFailureListener} class has the following methods:
 *
 * <ul>
 *     <li>{@link #onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent)} is an {@link EventListener} that listens for
 *     {@link AuthenticationFailureBadCredentialsEvent} events and updates the login attempts for the user associated with the event.
 *     If the principal of the authentication object in the event is a {@code String}, it is assumed to be the username of the
 *     user and is passed to the {@link LoginAttemptService#addUserToLoginAttemptCache(String)} method to update the login attempts
 *     for the user.</li>
 * </ul>
 */
@Component
public class AuthenticationFailureListener {
    private final LoginAttemptService loginAttemptService ;

    /**
     * Constructs a new {@code AuthenticationFailureListener} instance and initializes the {@code loginAttemptService} field
     * with the given {@link LoginAttemptService} instance.
     *
     * @param loginAttemptService the {@code LoginAttemptService} instance to use for tracking login attempts
     */
    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Listens for {@link AuthenticationFailureBadCredentialsEvent} events and updates the login attempts for the user
     * associated with the event. If the principal of the authentication object in the event is a {@code String}, it is
     * assumed to be the username of the user and is passed to the {@link LoginAttemptService#addUserToLoginAttemptCache(String)}
     * method to update the login attempts for the user.
     *
     * @param event the {@code AuthenticationFailureBadCredentialsEvent} event to process
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
