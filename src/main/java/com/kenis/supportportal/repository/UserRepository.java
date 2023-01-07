package com.kenis.supportportal.repository;

import com.kenis.supportportal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
*
 *The {@code UserRepository} interface is a Spring Data JPA repository for {@link User} entities. It provides
*
* methods for performing CRUD operations on the users table in the database.
*
* @author Mohamed Ali
*
 *@since 1.0
*
 *@see JpaRepository
*
 *@see User
 */
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByUserId(String userId);
}
