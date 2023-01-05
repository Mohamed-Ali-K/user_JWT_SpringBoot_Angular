package com.kenis.supportportal.repository;

import com.kenis.supportportal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUserName(String username);
    User findUserByUserEmail(String username);
}
