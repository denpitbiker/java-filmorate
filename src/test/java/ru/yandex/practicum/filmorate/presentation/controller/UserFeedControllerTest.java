package ru.yandex.practicum.filmorate.presentation.controller;

import java.io.UnsupportedEncodingException;

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
import ru.yandex.practicum.filmorate.data.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.data.model.enums.EventType;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_FILM_DTO_1;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_REVIEW_DTO;
import static ru.yandex.practicum.filmorate.domain.tool.StringToJsonConverter.asJsonString;

/**
 * Events logic has no separate controller, but it's including in many test classes seems too heavy
 * Main endpoint /users/userId/feed
 */
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserFeedControllerTest {

    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName("Get empty user feed after creation")
    public void get_users_feed_getUserFeed_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();

        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Get feed for non existing user")
    public void get_users_feed_getFeedForNonExistingUser_notFound404() throws Exception {
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get feed after make friends")
    public void get_users_feed_getFeedAfterMakeFriends_success200() throws Exception {
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();


        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2))
                .andExpect(status().isOk());

        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[*].userId", hasItem(userId1.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItem(userId2.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.FRIEND.name())))
                .andExpect(jsonPath("$.[*].operation", hasItem(EventOperation.ADD.name())));
    }

    /**
     * Sorry for such big test but it is necessary
     */
    @Test
    @DisplayName("Get user feed after add/remove friend")
    public void get_users_feed_getUserFeedAfterFriendActions_success200() throws Exception {
        // Create users
        Long userId1 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long userId2 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        Long userId3 = extractUserDto(addUser(TestStubs.VALID_USER_DTO_3.clone())).getId();

        // Add friends
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId1, userId2))
                .andExpect(status().isOk());
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId2, userId1))
                .andExpect(status().isOk());
        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId2, userId3))
                .andExpect(status().isOk());

        // Get feeds (must contain only add friend events)
        // for user1 feed will contain its make friend with user2 and also 2 make friends of user2
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].userId", hasItem(userId1.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItem(userId2.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.FRIEND.name())))
                .andExpect(jsonPath("$.[*].operation", hasItem(EventOperation.ADD.name())));
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId2))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[*].userId", hasItem(userId2.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItems(userId1.intValue(), userId3.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.FRIEND.name())))
                .andExpect(jsonPath("$.[*].operation", hasItem(EventOperation.ADD.name())));

        // Remove friends
        mvc.perform(delete(UserController.CONTROLLER_ROUTE + UserController.REMOVE_FRIEND_SUBROUTE, userId2, userId1))
                .andExpect(status().isOk());

        // Get feeds (must contain add and remove events)
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId2))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$.[*].userId", hasItem(userId2.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItems(userId1.intValue(), userId3.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.FRIEND.name())))
                .andExpect(jsonPath("$.[*].operation", hasItems(EventOperation.ADD.name(), EventOperation.REMOVE.name())));
    }

    @Test
    @DisplayName("Get feed after like film")
    public void get_users_feed_getFeedAfterLikeFilm_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();

        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId))
                .andExpect(status().isOk());

        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[*].userId", hasItem(userId.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItem(filmId.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.LIKE.name())))
                .andExpect(jsonPath("$.[*].operation", hasItem(EventOperation.ADD.name())));
    }

    @Test
    @DisplayName("Get feed after friend like film")
    public void get_users_feed_getFeedAfterUserLikeFilm_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long friendId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();

        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId, friendId))
                .andExpect(status().isOk());
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, friendId))
                .andExpect(status().isOk());

        // user feed must contain 2 events
        // add friend: EventType.FRIEND EventOperation.ADD
        // friend like: EventType.LIKE EventOperation.ADD
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].userId", hasItems(userId.intValue(), friendId.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItems(filmId.intValue(), friendId.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItems(EventType.LIKE.name(), EventType.FRIEND.name())))
                .andExpect(jsonPath("$.[*].operation", hasItems(EventOperation.ADD.name())));
    }

    @Test
    @DisplayName("Get empty feed after like non existing film")
    public void get_users_feed_getEmptyFeedAfterLikeNonExistingFilm_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();

        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, TestStubs.NON_EXISTING_ID, userId))
                .andExpect(status().isNotFound());

        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Get feed after unlike film")
    public void get_users_feed_getFeedAfterUnlikeFilm_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();

        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId))
                .andExpect(status().isOk());
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.UNLIKE_FILM_SUBROUTE, filmId, userId))
                .andExpect(status().isOk());

        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].userId", hasItem(userId.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItem(filmId.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.LIKE.name())))
                .andExpect(jsonPath("$.[*].operation", hasItems(EventOperation.ADD.name(), EventOperation.REMOVE.name())));
    }

    @Test
    @DisplayName("Get feed after create film review")
    public void get_users_feed_getFeedAfterCreateFilmReview_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        Long reviewId = extractReviewDto(addReview(VALID_REVIEW_DTO.clone())).getId();

        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[*].userId", hasItem(userId.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItem(reviewId.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.REVIEW.name())))
                .andExpect(jsonPath("$.[*].operation", hasItem(EventOperation.ADD.name())));
    }

    @Test
    @DisplayName("Get feed after friend create film review")
    public void get_users_feed_getFeedAfterFriendCreateFilmReview_success200() throws Exception {
        Long userId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_1.clone())).getId();
        Long friendId = extractUserDto(addUser(TestStubs.VALID_USER_DTO_2.clone())).getId();
        ReviewDto friendReview = VALID_REVIEW_DTO.clone();
        friendReview.setUserId(friendId);
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        Long reviewId = extractReviewDto(addReview(friendReview)).getId();

        mvc.perform(put(UserController.CONTROLLER_ROUTE + UserController.ADD_FRIEND_SUBROUTE, userId, friendId))
                .andExpect(status().isOk());

        // will contain events with making friend and friends review
        mvc.perform(get(UserController.CONTROLLER_ROUTE + UserController.GET_USER_FEED_SUBROUTE, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].userId", hasItem(friendId.intValue())))
                .andExpect(jsonPath("$.[*].entityId", hasItem(reviewId.intValue())))
                .andExpect(jsonPath("$.[*].eventType", hasItem(EventType.REVIEW.name())))
                .andExpect(jsonPath("$.[*].operation", hasItem(EventOperation.ADD.name())));
    }


    private ResultActions addUser(UserDto user) throws Exception {
        return mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(user)
                ));
    }

    private ResultActions addFilm(FilmDto film) throws Exception {
        return mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(film)
                ));
    }

    private ResultActions addReview(ReviewDto review) throws Exception {
        return mvc.perform(post(ReviewController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(review)
                ));
    }

    private UserDto extractUserDto(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        String userJson = actions
                .andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.readValue(userJson, UserDto.class);
    }


    private FilmDto extractFilmDto(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        String filmJson = actions
                .andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.readValue(filmJson, FilmDto.class);
    }

    private ReviewDto extractReviewDto(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        String reviewJson = actions
                .andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.readValue(reviewJson, ReviewDto.class);
    }

}
