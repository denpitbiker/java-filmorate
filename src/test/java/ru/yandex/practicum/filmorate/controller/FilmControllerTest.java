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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.yandex.practicum.filmorate.tool.StringToJsonConverter.asJsonString;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class FilmControllerTest {
    private static final String NAME_FIELD = "$.name";
    private static final String COUNT_ONE = "1";
    private static final String COUNT_MINUS_ONE = "-11";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    @Test
    @DisplayName("Remove like from existing film from existing user")
    public void delete_unlikeFilm_success200() throws Exception {
        Long filmId = filmStorage.addFilm(TestStubs.VALID_FILM_1.clone()).get().getId();
        Long userId = userStorage.addUser(TestStubs.VALID_USER_1.clone()).get().getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId));
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.UNLIKE_FILM_SUBROUTE, filmId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Remove like from existing film from non-existing user")
    public void delete_unlikeFilmNonExistingUser_notFound404() throws Exception {
        Long filmId = filmStorage.addFilm(TestStubs.VALID_FILM_1.clone()).get().getId();
        Long userId = userStorage.addUser(TestStubs.VALID_USER_1.clone()).get().getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId));
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.UNLIKE_FILM_SUBROUTE, filmId, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Remove like from non-existing film from existing user")
    public void delete_unlikeFilmNonExistinFilm_notFound404() throws Exception {
        Long filmId = filmStorage.addFilm(TestStubs.VALID_FILM_1.clone()).get().getId();
        Long userId = userStorage.addUser(TestStubs.VALID_USER_1.clone()).get().getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId));
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.UNLIKE_FILM_SUBROUTE, TestStubs.NON_EXISTING_ID, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add like to existing film from existing user")
    public void put_likeFilm_success200() throws Exception {
        Long filmId = filmStorage.addFilm(TestStubs.VALID_FILM_1.clone()).get().getId();
        Long userId = userStorage.addUser(TestStubs.VALID_USER_1.clone()).get().getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add like to existing film from non-existing user")
    public void put_likeFilm_nonExistingUser_notFound404() throws Exception {
        Long filmId = filmStorage.addFilm(TestStubs.VALID_FILM_1.clone()).get().getId();
        userStorage.addUser(TestStubs.VALID_USER_1.clone());
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add like to existing film from non-existing film")
    public void put_likeFilm_nonExistingFilm_notFound404() throws Exception {
        filmStorage.addFilm(TestStubs.VALID_FILM_1.clone());
        Long userId = userStorage.addUser(TestStubs.VALID_USER_1.clone()).get().getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, TestStubs.NON_EXISTING_ID, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get invalid films top")
    public void get_topFilm_badRequest400() throws Exception {
        filmStorage.addFilm(TestStubs.VALID_FILM_1.clone());
        filmStorage.addFilm(TestStubs.VALID_FILM_2.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_TOP_FILMS_SUBROUTE)
                        .param(FilmController.COUNT_PARAM, COUNT_MINUS_ONE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get top 1 film (count provided)")
    public void get_top1Film_success200() throws Exception {
        filmStorage.addFilm(TestStubs.VALID_FILM_1.clone());
        filmStorage.addFilm(TestStubs.VALID_FILM_2.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_TOP_FILMS_SUBROUTE)
                        .param(FilmController.COUNT_PARAM, COUNT_ONE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get top films (count not provided)")
    public void get_topFilms_success200() throws Exception {
        filmStorage.addFilm(TestStubs.VALID_FILM_1.clone());
        filmStorage.addFilm(TestStubs.VALID_FILM_2.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_TOP_FILMS_SUBROUTE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get a specific film with valid id")
    public void get_film_validId_success200() throws Exception {
        Long filmId = filmStorage.addFilm(TestStubs.VALID_FILM_1.clone()).get().getId();
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_FILM_SUBROUTE, filmId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(TestStubs.VALID_FILM_1.getName()));
    }

    @Test
    @DisplayName("Get a specific film with invalid id")
    public void get_film_invalidId_notFound404() throws Exception {
        filmStorage.addFilm(TestStubs.VALID_FILM_1.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_FILM_SUBROUTE, TestStubs.NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add new film (valid film)")
    public void post_films_addValidFilm_success201WithDto() throws Exception {
        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.VALID_FILM_1.clone())
                        ))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(TestStubs.VALID_FILM_NAME_1));
        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.VALID_FILM_2.clone())
                        ))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(TestStubs.VALID_FILM_NAME_2));
    }

    @Test
    @DisplayName("Add new film with incorrect field (null name)")
    public void post_films_addFilmWithNullName_fail400() throws Exception {
        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.INVALID_FILM_NULL_NAME.clone())
                        ))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add already existing film")
    public void post_films_addExistingFilm_conflict409() throws Exception {
        String filmJson = mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_FILM_1.clone())
                )).andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        Film filmWithId = om.readValue(filmJson, Film.class);

        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(filmWithId)
                        ))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Update existing film")
    public void put_films_updateExistingFilm_success200() throws Exception {
        String film1Json = mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_FILM_1.clone())
                )).andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        Film film1WithId = om.readValue(film1Json, Film.class);
        film1WithId.setName(TestStubs.VALID_FILM_NAME_2);

        mvc.perform(put(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(film1WithId)
                        ))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(TestStubs.VALID_FILM_NAME_2));
    }

    @Test
    @DisplayName("Update non-existing film")
    public void put_films_updateNonExistingFilm_notFound404() throws Exception {
        mvc.perform(put(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.VALID_FILM_2.clone())
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all films (not empty repository)")
    public void get_films_getFilmsFromNotEmptyRepository_success200WithDto() throws Exception {
        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_FILM_1.clone())
                ));
        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(TestStubs.VALID_FILM_2.clone())
                ));
        mvc.perform(get(FilmController.CONTROLLER_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
