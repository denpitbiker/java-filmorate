package ru.yandex.practicum.filmorate.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.TestStubs;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.yandex.practicum.filmorate.domain.tool.StringToJsonConverter.asJsonString;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String NAME_FIELD = "$.name";

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Get friends from existing user")
    public void get_userFriends_existingUser_success200() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2));
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_FRIENDS_SUBROUTE, userId1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get friends from non-existing user")
    public void get_userFriends_nonExistingUser_notFound404() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2));
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_FRIENDS_SUBROUTE, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get common friends with both existing users")
    public void get_usersCommonFriends_existingBothUsers_success200() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        Long userId3 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_3.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId3));
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId2, userId3));
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_COMMON_FRIENDS_SUBROUTE, userId1, userId2))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get common friends with first non-existing user")
    public void get_usersCommonFriends_onlySecondUserExists_notFound404() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        Long userId3 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_3.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId3));
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId2, userId3));
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_COMMON_FRIENDS_SUBROUTE, TestStubs.NON_EXISTING_ID, userId2))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get common friends with second non-existing user")
    public void get_usersCommonFriends_onlyFirstUserExists_notFound404() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        Long userId3 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_3.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId3));
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId2, userId3));
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_COMMON_FRIENDS_SUBROUTE, userId1, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Remove friend from user, both are in storage")
    public void delete_removeFriend_existingFriendAndUser_success200() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2));
        mvc.perform(delete(UserController.CONTROLLER_ROUTE + UserController.REMOVE_FRIEND_SUBROUTE, userId1, userId2))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Remove friend from user, only user in storage")
    public void delete_removeFriend_existingUserNonExistingFriend_notFound404() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2));
        mvc.perform(delete(UserController.CONTROLLER_ROUTE + UserController.REMOVE_FRIEND_SUBROUTE, userId1, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Remove friend from user, only friend in storage")
    public void delete_removeFriend_existingFriendNonExistingUser_notFound404() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2));
        mvc.perform(delete(UserController.CONTROLLER_ROUTE + UserController.REMOVE_FRIEND_SUBROUTE, TestStubs.NON_EXISTING_ID, userId2))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Remove user itself from friends")
    public void delete_removeFriend_userRemovedItselfFromFriends_badRequest400() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        mvc.perform(delete(UserController.CONTROLLER_ROUTE + UserController.REMOVE_FRIEND_SUBROUTE, userId1, userId1))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add friend to user, both are in storage")
    public void put_addFriend_existingFriendAndUser_success200() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add friend to user, only user in storage")
    public void put_addFriend_existingUserNonExistingFriend_notFound404() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add friend to user, only friend in storage")
    public void put_addFriend_existingFriendNonExistingUser_notFound404() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, TestStubs.NON_EXISTING_ID, userId1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add user itself to friends")
    public void put_addFriend_userAddedItselfToFriends_badRequest400() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId1))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get a specific user with valid id")
    public void get_user_validId_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(TestStubs.VALID_USER_DTO_1.getName()));
    }

    @Test
    @DisplayName("Get a specific user with invalid id")
    public void get_user_invalidId_notFound404() throws Exception {
        addUser(TestStubs.VALID_USER_DTO_1.clone());
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_SUBROUTE, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add new user (valid user)")
    public void post_users_addValidUser_success201WithDto() throws Exception {
        addUser(TestStubs.VALID_USER_DTO_1.clone())
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(TestStubs.VALID_USER_NAME_1));
    }

    @Test
    @DisplayName("Add new user with incorrect field (null login)")
    public void post_users_addUserWithNullLogin_fail400() throws Exception {
        addUser(TestStubs.INVALID_USER_DTO_NULL_LOGIN.clone())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add already existing user")
    public void post_users_addExistingUser_conflict409() throws Exception {
        UserDto userWithId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone()));

        mvc.perform(post(UserController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(userWithId)
                        ))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Update existing user")
    public void put_users_updateExistingUser_success200() throws Exception {
        UserDto userWithId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone()));
        userWithId.setName(TestStubs.VALID_USER_NAME_2);

        mvc.perform(put(UserController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(userWithId)
                        ))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(TestStubs.VALID_USER_NAME_2));
    }

    @Test
    @DisplayName("Update non-existing user")
    public void put_users_updateNonExistingUser_notFound404() throws Exception {
        mvc.perform(put(UserController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.VALID_USER_DTO_1.clone())
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all users from not empty repository")
    public void get_users_getUsersFromNotEmptyRepository_success200WithDto() throws Exception {
        addUser(TestStubs.VALID_USER_DTO_1.clone());
        addUser(TestStubs.VALID_USER_DTO_2.clone());
        mvc.perform(get(UserController.CONTROLLER_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private ResultActions addUser(UserDto user) throws Exception {
       return mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(user)
                ));
    }

    private UserDto extractUserDto(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
         String userJson = actions
                .andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.readValue(userJson, UserDto.class);
    }
}
