package com.kenis.usermanager;

import com.kenis.usermanager.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;


import org.springframework.mock.web.MockMultipartFile;
import com.kenis.usermanager.constant.FileConstant;


public class UserCrudIntegrationTest extends BaseIntegrationTest {

    @Test
    void testAddNewUser_Success() throws Exception {
        // Create an admin user to perform the action
        User adminUser = createTestUser("crud_admin_add", "adminPass123", "crud_admin_add@example.com", "ROLE_ADMIN");
        String adminToken = getAuthToken(adminUser.getUsername(), "adminPass123");

        String newUserUsername = "newlyaddeduser";
        String newUserEmail = "newlyadded@example.com";

        mockMvc.perform(post("/user/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Newly")
                        .param("lastName", "Added")
                        .param("username", newUserUsername)
                        .param("email", newUserEmail)
                        .param("role", "ROLE_USER")
                        .param("isActive", "true")
                        .param("isNotLock", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(newUserUsername)))
                .andExpect(jsonPath("$.email", is(newUserEmail)))
                .andExpect(jsonPath("$.role", is("ROLE_USER")));
    }

    @Test
    void testAddNewUser_Unauthorized_MissingToken() throws Exception {
        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "NoToken")
                        .param("lastName", "User")
                        .param("username", "notokenuser")
                        .param("email", "notoken@example.com")
                        .param("role", "ROLE_USER")
                        .param("isActive", "true")
                        .param("isNotLock", "true"))
                .andExpect(status().isForbidden()); // Spring Security typically returns 403 if Authorization header is missing for a secured endpoint
    }

    @Test
    void testAddNewUser_Forbidden_InsufficientPrivileges() throws Exception {
        // Create a regular user
        User regularUser = createTestUser("crud_regular_user", "regPass123", "crud_regular@example.com", "ROLE_USER");
        String regularToken = getAuthToken(regularUser.getUsername(), "regPass123");

        mockMvc.perform(post("/user/add")
                        .header("Authorization", "Bearer " + regularToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Attempted")
                        .param("lastName", "Add")
                        .param("username", "attemptedadd")
                        .param("email", "attempted@example.com")
                        .param("role", "ROLE_USER")
                        .param("isActive", "true")
                        .param("isNotLock", "true"))
                .andExpect(status().isForbidden()); // Expecting 403 as ROLE_USER should not add users if endpoint is admin-only
                                                    // If this fails (e.g. returns 200), it means any authenticated user can add.
    }


    @Test
    void testGetUser_ByUsername_Success() throws Exception {
        User targetUser = createTestUser("findmeuser", "findPass123", "findme@example.com", "ROLE_USER");
        // User performing the action
        User searchingUser = createTestUser("searcheruser", "searchPass123", "searcher@example.com", "ROLE_USER");
        String searcherToken = getAuthToken(searchingUser.getUsername(), "searchPass123");

        mockMvc.perform(get("/user/find/" + targetUser.getUsername())
                        .header("Authorization", "Bearer " + searcherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(targetUser.getUsername())))
                .andExpect(jsonPath("$.email", is(targetUser.getEmail())));
    }

    @Test
    void testListUsers_Success() throws Exception {
        User adminUser = createTestUser("crud_admin_list", "listPass123", "crud_admin_list@example.com", "ROLE_ADMIN");
        String adminToken = getAuthToken(adminUser.getUsername(), "listPass123");

        createTestUser("listuser1", "listPass1", "list1@example.com", "ROLE_USER");
        createTestUser("listuser2", "listPass2", "list2@example.com", "ROLE_USER");

        mockMvc.perform(get("/user/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", instanceOf(java.util.List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
                // Could also check for specific users if usernames are unique and predictable
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        User adminUser = createTestUser("crud_admin_update", "updateAdminPass", "crud_admin_update@example.com", "ROLE_ADMIN");
        String adminToken = getAuthToken(adminUser.getUsername(), "updateAdminPass");

        User userToUpdate = createTestUser("updatetarget", "oldPass", "updateoriginal@example.com", "ROLE_USER");

        String newEmail = "updatedemail@example.com";
        String newFirstName = "UpdatedFirst";

        mockMvc.perform(post("/user/update")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("currentUsername", userToUpdate.getUsername())
                        .param("firstName", newFirstName)
                        .param("lastName", userToUpdate.getLastName()) // Assuming last name doesn't change or provide new
                        .param("username", userToUpdate.getUsername())   // Assuming username doesn't change or provide new
                        .param("email", newEmail)
                        .param("role", userToUpdate.getRole())         // Assuming role doesn't change or provide new
                        .param("isActive", String.valueOf(userToUpdate.getIsActive()))
                        .param("isNotLock", String.valueOf(userToUpdate.getIsNotLocked())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(newEmail)))
                .andExpect(jsonPath("$.firstName", is(newFirstName)));

        // Optional: Verify update persisted
        mockMvc.perform(get("/user/find/" + userToUpdate.getUsername())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(newEmail)))
                .andExpect(jsonPath("$.firstName", is(newFirstName)));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // User with delete privileges
        User superAdmin = createTestUser("crud_superadmin_delete", "superDelPass", "crud_superadmin_del@example.com", "ROLE_SUPER_ADMIN");
        String superAdminToken = getAuthToken(superAdmin.getUsername(), "superDelPass");

        User userToDelete = createTestUser("todeleteuser", "delPass123", "todelete@example.com", "ROLE_USER");
        // We need user's ID. The createTestUser returns the User object which should have the ID after save.
        // If not, we'd have to find the user first to get their ID. Let's assume createTestUser populates the ID.
        // User userToDeleteFetched = userService.findUserByUsername(userToDelete.getUsername()); // Ensure ID is populated


        mockMvc.perform(delete("/user/delete/" + userToDelete.getId())
                        .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isNoContent()); // Controller returns NO_CONTENT (204)

        // Optional: Verify user is deleted
        mockMvc.perform(get("/user/find/" + userToDelete.getUsername())
                        .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isBadRequest()); // UserNotFoundException is handled by ExceptionHandling to return BAD_REQUEST
    }

    @Test
    void testDeleteUser_Forbidden_InsufficientPrivileges() throws Exception {
        User regularUser = createTestUser("nodeletepriv", "noDelPass", "nodel@example.com", "ROLE_USER");
        String regularToken = getAuthToken(regularUser.getUsername(), "noDelPass");

        User targetUser = createTestUser("targetfordel", "targetPass", "targetdel@example.com", "ROLE_USER");
        // User targetUserFetched = userService.findUserByUsername(targetUser.getUsername());

        mockMvc.perform(delete("/user/delete/" + targetUser.getId())
                        .header("Authorization", "Bearer " + regularToken))
                .andExpect(status().isForbidden()); // ROLE_USER does not have 'user:delete'
    }

    @Test
    void testUpdateProfileImage_Success() throws Exception {
        String username = "imageuser_update";
        User user = createTestUser(username, "imagePass123", "imageupdate@example.com", "ROLE_USER");
        String token = getAuthToken(username, "imagePass123");

        byte[] imageContent = "test image content for update".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "newProfileImage",      // Matches @RequestParam(" newProfileImage") - note the space in controller
                username + ".jpg", // Original filename
                MediaType.IMAGE_JPEG_VALUE,
                imageContent
        );
        
        // The @RequestParam in UserResource.java for updateProfileImage has a leading space for some reason:
        // @RequestParam(value = " newProfileImage")
        // So, the name in MockMultipartFile should match " newProfileImage" (with space) or the param name in controller should be fixed.
        // For now, assuming the test will use the exact param name as in the controller.
        // After checking UserResource.java, the param is: @RequestParam(value = " newProfileImage")
        // Let's try to match that, if it fails, we might need to adjust the param name in the controller.
        // The tool created the file with "newProfileImage" as the name for the part.
        // The actual parameter name in the controller is ` newProfileImage` (with a leading space).
        // Let's try with the actual name from controller.

        MockMultipartFile mockFileCorrectedName = new MockMultipartFile(
            " newProfileImage", // Parameter name from @RequestParam in controller
            username + ".jpg",
            MediaType.IMAGE_JPEG_VALUE,
            imageContent
        );


        mockMvc.perform(multipart("/user/updateProfileImage")
                        .file(mockFileCorrectedName)
                        .param("username", username)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.profileImageUrl", notNullValue()))
                .andExpect(jsonPath("$.profileImageUrl", endsWith(FileConstant.USER_IMAGE_PATH + username + "/" + username + FileConstant.DOT + FileConstant.JPG_EXTENSION)));
    }

    @Test
    void testGetProfileImage_Success() throws Exception {
        String username = "imageuser_get";
        User user = createTestUser(username, "imageGetPass123", "imageget@example.com", "ROLE_USER");
        String token = getAuthToken(username, "imageGetPass123");

        byte[] imageContent = "test image content for get".getBytes();
        // Parameter name for newProfileImage in UserResource is " newProfileImage" (with a leading space)
        MockMultipartFile mockFile = new MockMultipartFile(
                " newProfileImage", // Matches @RequestParam(" newProfileImage") in controller
                username + ".jpg",
                MediaType.IMAGE_JPEG_VALUE,
                imageContent
        );

        // 1. Upload the image first
        mockMvc.perform(multipart("/user/updateProfileImage")
                        .file(mockFile)
                        .param("username", username)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        // 2. Construct the expected image path
        // The UserServiceImpl saves the image as {username}.jpg in the folder {USER_FOLDER}/{username}
        // The UserResource constructs the URL as /user/image/{username}/{fileName}
        // where fileName is {username}.jpg
        String expectedImageFileName = username + FileConstant.DOT + FileConstant.JPG_EXTENSION;
        String imageUrl = FileConstant.FORWARD_SLASH + "user" + FileConstant.IMAGE_PATH + username + FileConstant.FORWARD_SLASH + expectedImageFileName;
        // imageUrl should be like /user/image/imageuser_get/imageuser_get.jpg

        // 3. Perform GET request to retrieve the image
        mockMvc.perform(get(imageUrl)) // No token needed as per current endpoint security
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(result -> {
                    byte[] responseBody = result.getResponse().getContentAsByteArray();
                    assertArrayEquals(imageContent, responseBody, "Image content should match uploaded content.");
                });
    }
}
