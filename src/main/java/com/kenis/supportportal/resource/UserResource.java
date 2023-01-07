package com.kenis.supportportal.resource;




import com.kenis.supportportal.domain.User;
import com.kenis.supportportal.exception.domain.EmailExistException;
import com.kenis.supportportal.exception.domain.ExceptionHandling;
import com.kenis.supportportal.exception.domain.UserNameExistException;
import com.kenis.supportportal.exception.domain.UserNotFoundException;
import com.kenis.supportportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserResource extends ExceptionHandling {
    private UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UserNameExistException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUserName());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
}
