package ru.yandex.practicum.filmorate.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.yandex.practicum.filmorate.TestStubs.*;
import static ru.yandex.practicum.filmorate.domain.tool.StringToJsonConverter.asJsonString;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;
    private static final String COUNT_FIVE_PARAM = "5";

    private FilmDto film;
    private UserDto user;

    @BeforeEach
    public void setUp() throws Exception {
        film = extractFilmDto(addFilm());
        user = extractUserDto(addUser());
    }

    @Test
    @DisplayName("Create review - success")
    public void post_createReview_success201() throws Exception {
        addReview()
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(VALID_REVIEW_DTO.getContent()));
    }

    @Test
    @DisplayName("Update review - success")
    public void put_updateReview_success200() throws Exception {
        ReviewDto added = extractReviewDto(addReview());
        added.setContent(TestStubs.VALID_FILM_DESCRIPTION_1);
        mvc.perform(put(ReviewController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(added)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete review - success")
    public void delete_review_success200() throws Exception {
        ReviewDto added = extractReviewDto(addReview());
        mvc.perform(delete(ReviewController.CONTROLLER_ROUTE + ReviewController.DELETE_REVIEW_SUBROUTE, added.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get review - success")
    public void get_review_success200() throws Exception {
        ReviewDto added = extractReviewDto(addReview());
        mvc.perform(get(ReviewController.CONTROLLER_ROUTE + ReviewController.GET_REVIEW_SUBROUTE, added.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(VALID_REVIEW_DTO.getContent()));
    }

    @Test
    @DisplayName("Add like to review - success")
    public void put_addLike_success200() throws Exception {
        ReviewDto added = extractReviewDto(addReview());
        mvc.perform(put(ReviewController.CONTROLLER_ROUTE + ReviewController.ADD_LIKE_SUBROUTE, added.getId(), user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Remove like from review - success")
    public void delete_removeLike_success200() throws Exception {
        ReviewDto added = extractReviewDto(addReview());
        mvc.perform(put(ReviewController.CONTROLLER_ROUTE + ReviewController.ADD_LIKE_SUBROUTE, added.getId(), user.getId()));

        mvc.perform(delete(ReviewController.CONTROLLER_ROUTE + ReviewController.REMOVE_LIKE_SUBROUTE, added.getId(), user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add dislike to review - success")
    public void put_addDislike_success200() throws Exception {
        ReviewDto added = extractReviewDto(addReview());
        mvc.perform(put(ReviewController.CONTROLLER_ROUTE + ReviewController.ADD_DISLIKE_SUBROUTE, added.getId(), user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Remove dislike from review - success")
    public void delete_removeDislike_success200() throws Exception {
        ReviewDto added = extractReviewDto(addReview());
        mvc.perform(put(ReviewController.CONTROLLER_ROUTE + ReviewController.ADD_DISLIKE_SUBROUTE, added.getId(), user.getId()));

        mvc.perform(delete(ReviewController.CONTROLLER_ROUTE + ReviewController.REMOVE_DISLIKE_SUBROUTE, added.getId(), user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get all reviews - success")
    public void get_allReviews_success200() throws Exception {
        addReview();

        mvc.perform(get(ReviewController.CONTROLLER_ROUTE)
                        .param(ReviewController.FILM_ID_PARAM, film.getId().toString())
                        .param(ReviewController.COUNT_PARAM, COUNT_FIVE_PARAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Get all reviews - success with default count")
    public void get_allReviews_defaultCount_success200() throws Exception {
        addReview();

        mvc.perform(get(ReviewController.CONTROLLER_ROUTE)
                        .param(ReviewController.FILM_ID_PARAM, film.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Get all reviews - success with no filmId")
    public void get_allReviews_noFilmId_success200() throws Exception {
        addReview();

        mvc.perform(get(ReviewController.CONTROLLER_ROUTE)
                        .param(ReviewController.COUNT_PARAM, COUNT_FIVE_PARAM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Get all reviews - success with no filmId and count")
    public void get_allReviews_noFilmIdAndCount_success200() throws Exception {
        addReview();

        mvc.perform(get(ReviewController.CONTROLLER_ROUTE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private ResultActions addReview() throws Exception {
        return mvc.perform(post(ReviewController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_REVIEW_DTO)
                ));
    }

    private ResultActions addFilm() throws Exception {
        return mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_FILM_DTO_1)
                ));
    }

    private ResultActions addUser() throws Exception {
        return mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_USER_DTO_1)
                ));
    }

    private ReviewDto extractReviewDto(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        String reviewJson = actions
                .andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.readValue(reviewJson, ReviewDto.class);
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
}
