package com.kenis.supportportal.repository;

import com.kenis.supportportal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUserName(String username);
    User findUserByEmail(String email);
}
