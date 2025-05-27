package com.kenis.usermanager.repository;

import com.kenis.usermanager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The {@code UserRepository} interface is a Spring Data JPA repository for {@link User} entities.
 * It provides methods for performing CRUD operations on the users table in the database.
 *
 * @author Mohamed Ali
 * @since 1.0
 * @see JpaRepository
 * @see User
 */
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The {@code User} object if found, otherwise {@code null}.
     */
    User findUserByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for.
     * @return The {@code User} object if found, otherwise {@code null}.
     */
    User findUserByEmail(String email);

    /**
     * Finds a user by their unique user ID.
     *
     * @param userId The user ID to search for.
     * @return The {@code User} object if found, otherwise {@code null}.
     */
    User findUserByUserId(String userId);

    /**
     * Finds a user by their primary key (database ID).
     *
     * @param id The ID to search for.
     * @return The {@code User} object if found, otherwise {@code null}.
     */
    User findUserById(Long id);
}
