package com.kenis.supportportal.resource;


import com.kenis.supportportal.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

    @GetMapping("/home")
    public String showUser(){
        return "Application Work";
    }
}
