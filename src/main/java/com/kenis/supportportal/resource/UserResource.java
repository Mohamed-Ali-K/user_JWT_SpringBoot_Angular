package com.kenis.supportportal.resource;




import com.kenis.supportportal.exception.domain.ExceptionHandling;
import com.kenis.supportportal.exception.domain.UserNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserResource extends ExceptionHandling {

    @GetMapping("/home")
    public String showUser() throws UserNotFoundException{
        //return "Application Work";
        throw new UserNotFoundException("User Not Found Exception");
    }
}
