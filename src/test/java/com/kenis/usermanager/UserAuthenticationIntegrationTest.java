package com.kenis.usermanager;

import com.kenis.usermanager.constant.SecurityConstant;
import com.kenis.usermanager.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


public class UserAuthenticationIntegrationTest extends BaseIntegrationTest {

    @Test
    void testRegister_Success() throws Exception {
        User newUser = new User();
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setUsername("testuser_success");
        newUser.setEmail("testuser_success@example.com");
        // Password is not set here, as the service is expected to generate it.

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is("testuser_success")))
                .andExpect(jsonPath("$.email", is("testuser_success@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist()); // Or .andExpect(jsonPath("$.password", nullValue()));
    }

    @Test
    void testRegister_UsernameAlreadyExists() throws Exception {
        // First, register a user
        User user1 = new User();
        user1.setFirstName("Existing");
        user1.setLastName("User");
        user1.setUsername("existingusername");
        user1.setEmail("existing_username@example.com");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        // Attempt to register another user with the same username
        User user2 = new User();
        user2.setFirstName("Another");
        user2.setLastName("Person");
        user2.setUsername("existingusername"); // Same username
        user2.setEmail("another_person@example.com"); // Different email

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isBadRequest()); // UsernameExistException maps to 400
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        // First, register a user
        User user1 = new User();
        user1.setFirstName("Some");
        user1.setLastName("One");
        user1.setUsername("someone");
        user1.setEmail("existing_email@example.com");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        // Attempt to register another user with the same email
        User user2 = new User();
        user2.setFirstName("Different");
        user2.setLastName("Name");
        user2.setUsername("differentname"); // Different username
        user2.setEmail("existing_email@example.com"); // Same email

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isBadRequest()); // EmailExistException maps to 400
    }

    @Test
    void testLogin_Success() throws Exception {
        String username = "loginuser_success";
        String password = "testPass123";
        String email = "login_success@example.com";
        createTestUser(username, password, email, "ROLE_USER");

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists(SecurityConstant.JWT_TOKEN_HEADER))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void testLogin_InvalidCredentials_UserNotFound() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nosuchuser");
        loginRequest.put("password", "anypassword");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Spring Security's default for bad credentials (user not found or bad password)
    }

    @Test
    void testLogin_InvalidCredentials_IncorrectPassword() throws Exception {
        String username = "loginfailuser";
        String correctPassword = "correctPass123";
        String incorrectPassword = "wrongPassword123";
        String email = "loginfail@example.com";
        createTestUser(username, correctPassword, email, "ROLE_USER");

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", incorrectPassword);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Spring Security's default for bad credentials
    }
}
