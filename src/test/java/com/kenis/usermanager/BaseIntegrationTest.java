package com.kenis.usermanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.kenis.usermanager.service.UserService;
import com.kenis.usermanager.domain.User;
import com.kenis.usermanager.constant.SecurityConstant;


import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserService userService;

    @BeforeEach
    void setUp() {
        // objectMapper = new ObjectMapper(); // It's autowired, so manual instantiation is not typically needed here.
                                        // If specific custom configuration for ObjectMapper is needed for tests,
                                        // it can be done here or via a @TestConfiguration class.
    }

    protected User createTestUser(String username, String password, String email, String role) throws Exception {
        // Consider making this more robust, perhaps deleting the user if they exist from a previous failed test run.
        // For now, assume userService.createUserForTest will handle existing user validation or tests will use unique users.
        return userService.createUserForTest(
            "Test", "User", username, email, password, role
        );
    }

    protected String getAuthToken(String username, String password) throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        MvcResult result = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getHeader(SecurityConstant.JWT_TOKEN_HEADER);
    }
}
