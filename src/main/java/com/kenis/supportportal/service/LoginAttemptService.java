package com.kenis.supportportal.service;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * <h3> Service that tracks login attempts for users.
 *
 * <p> A cache is a storage location that holds data that is expensive to compute or retrieve. By storing the data in a cache,
 * future requests for that data can be served more quickly because the data is already available.
 * <p> When a value is
 * requested from a {@link LoadingCache}, it first checks if the value is present in the cache. If it is, the value is
 * returned from the cache. If the value is not present in the cache, the {@link CacheLoader} specified when the
 * {@code LoadingCache} was created is used to load the value into the cache and return it to the caller.
 *
 * <p> In the {@code LoginAttemptService} class, the {@code LoadingCache} is used to store the number of login attempts made
 * by each user. It has a maximum size of 100 and entries expire after 15 minutes.
 * <p> When a user's login attempts are requested, the {@code LoadingCache} checks if the user is present in the cache. If they are, their login attempts are
 * returned. If the user is not present in the cache, the {@code CacheLoader} is used to add the user to the cache with
 * 0 login attempts.
 * <p>
 * The {@code LoginAttemptService} class has the following constructors:
 *
 * <ul>
 *    <li>{@link #LoginAttemptService()} constructs a new {@code LoginAttemptService} instance and initializes the
 *      {@code loginAttemptCache} field with a {@code LoadingCache} configured to expire entries after 15 minutes and
 *     store a maximum of 100 entries. The {@code CacheLoader} for the {@code LoadingCache} returns 0 for any key that
 *    is not present in the cache.</li>
 * </ul>
 */
@Service
public class LoginAttemptService {
    /**
     * Maximum number of allowed login attempts before a user is considered to have exceeded the limit.
     */
    private static final Integer MAXIMUM_NUMBER_OF_ATTEMPTS = 5;

    /**
     * Number by which to increment the login attempts for a user.
     */
    private static final Integer ATTEMPTS_INCREMENT = 1;

    /**
     * Cache for storing login attempts for users. The keys are usernames and the values are the number of login attempts.
     */
    private final LoadingCache<String, Integer> loginAttemptCache;

    /**
     * Constructs a new {@code LoginAttemptService} instance and initializes the {@code loginAttemptCache} field with a
     * {@code LoadingCache} configured to expire entries after 15 minutes and store a maximum of 100 entries. The
     * {@code CacheLoader} for the {@code LoadingCache} returns 0 for any key that is not present in the cache.
     */
    public LoginAttemptService() {
        super();
        this.loginAttemptCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    /**
     * Removes a user from the login attempts cache.
     *
     * @param username the username of the user to remove from the cache
     */
    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    /**
     * Adds a user to the login attempts cache or increments their login attempts if they are already in the cache.
     *
     * @param username the username of the user to add to the cache or increment the login attempts for
     * @throws ExecutionException if an exception is thrown while getting the current login attempts for the user
     */
    public void addUserToLoginAttemptCache(String username) throws ExecutionException {
        int attempts = 0;
        attempts = ATTEMPTS_INCREMENT + loginAttemptCache.get(username);
        loginAttemptCache.put(username, attempts);

    }

    /**
     * Determines whether a user has exceeded the maximum allowed login attempts.
     *
     * @param username the username of the user to check
     * @return {@code true} if the user has exceeded the maximum allowed login attempts, {@code false} otherwise
     * @throws ExecutionException if an exception is thrown while getting the current login attempts for the user
     */
    public boolean hasExceededMaxAttempts(String username) throws ExecutionException {
        return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
    }
}
