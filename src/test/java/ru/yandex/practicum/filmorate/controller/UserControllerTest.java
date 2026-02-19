package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.TestStubs;
import ru.yandex.practicum.filmorate.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.yandex.practicum.filmorate.tool.StringToJsonConverter.asJsonString;

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
    @DisplayName("Add new user (valid user)")
    public void post_users_addValidUser_success201WithDto() throws Exception {
        mvc.perform(post(UserController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.VALID_USER_1.clone())
                        ))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(TestStubs.VALID_USER_NAME_1));
    }

    @Test
    @DisplayName("Add new user with incorrect field (null login)")
    public void post_users_addUserWithNullLogin_fail400() throws Exception {
        mvc.perform(post(UserController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.INVALID_USER_NULL_LOGIN.clone())
                        ))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add already existing user")
    public void post_users_addExistingUser_conflict409() throws Exception {
        String userJson = mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_USER_1.clone())
                )).andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        User userWithId = om.readValue(userJson, User.class);

        mvc.perform(post(UserController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(userWithId)
                        ))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Update existing user")
    public void put_users_updateExistingUser_success200() throws Exception {
        String userJson = mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_USER_1.clone())
                )).andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        User userWithId = om.readValue(userJson, User.class);
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
                                asJsonString(TestStubs.VALID_USER_1.clone())
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all users from not empty repository")
    public void get_users_getUsersFromNotEmptyRepository_success200WithDto() throws Exception {
        mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_USER_1.clone())
                ));
        mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_USER_2.clone())
                ));
        mvc.perform(get(UserController.CONTROLLER_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
