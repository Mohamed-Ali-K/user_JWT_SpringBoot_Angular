package com.kenis.usermanager.listener;

import com.kenis.usermanager.domain.User;
import com.kenis.usermanager.domain.UserPrincipal;
import com.kenis.usermanager.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;


/**
 * <h3>Listener that processes {@link AuthenticationSuccessEvent} events.</h3>
 *
 * <p> When an {@code AuthenticationSuccessEvent} is received, this listener removes the user associated with the event
 * from the login attempts cache. If the principal of the authentication object in the event is a {@link User} object, it
 * is assumed to be the user who successfully logged in and their username is passed to the
 * {@link LoginAttemptService#evictUserFromLoginAttemptCache(String)} method to remove them from the cache.
 *
 * <p> The {@code AuthenticationSuccessListener} class has the following constructors:
 *
 * <ul>
 *     <li>{@link #AuthenticationSuccessListener(LoginAttemptService)} constructs a new
 *     {@code AuthenticationSuccessListener} instance and initializes the {@code loginAttemptService} field with the given
 *     {@link LoginAttemptService} instance.</li>
 * </ul>
 */
@Component
public class AuthenticationSuccessListener {

    /**
     * Service for tracking login attempts for users.
     */
    private final LoginAttemptService loginAttemptService ;

    /**
     * Constructs a new {@code AuthenticationFailureListener} instance and initializes the {@code loginAttemptService} field
     * with the given {@link LoginAttemptService} instance.
     *
     * @param loginAttemptService the {@code LoginAttemptService} instance to use for tracking login attempts
     */
    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Listens for {@link AuthenticationSuccessEvent} events and removes the user associated with the event from the login
     * attempts cache. If the principal of the authentication object in the event is a {@link User} object, it is assumed
     * to be the user who successfully logged in and their username is passed to the
     * {@link LoginAttemptService#evictUserFromLoginAttemptCache(String)} method to remove them from the cache.
     *
     * @param event the {@code AuthenticationSuccessEvent} event to process
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
