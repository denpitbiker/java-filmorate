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
import ru.yandex.practicum.filmorate.presentation.dto.DirectorDto;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.io.UnsupportedEncodingException;
import java.util.Set;

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
public class FilmControllerTest {
    private static final String NAME_FIELD = "$.name";
    private static final String COUNT_ONE = "1";
    private static final String COUNT_MINUS_ONE = "-11";

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Remove like from existing film from existing user")
    public void delete_unlikeFilm_success200() throws Exception {
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        Long userId = extractUserDto(addUser(VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId));
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.UNLIKE_FILM_SUBROUTE, filmId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Remove like from existing film from non-existing user")
    public void delete_unlikeFilmNonExistingUser_notFound404() throws Exception {
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        Long userId = extractUserDto(addUser(VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId));
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.UNLIKE_FILM_SUBROUTE, filmId, NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Remove like from non-existing film from existing user")
    public void delete_unlikeFilmNonExistinFilm_notFound404() throws Exception {
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        Long userId = extractUserDto(addUser(VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId));
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.UNLIKE_FILM_SUBROUTE, NON_EXISTING_ID, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add like to existing film from existing user")
    public void put_likeFilm_success200() throws Exception {
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        Long userId = extractUserDto(addUser(VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add like to existing film from non-existing user")
    public void put_likeFilm_nonExistingUser_notFound404() throws Exception {
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        addUser(VALID_USER_DTO_1.clone());
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, filmId, NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add like to existing film from non-existing film")
    public void put_likeFilm_nonExistingFilm_notFound404() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone());
        Long userId = extractUserDto(addUser(VALID_USER_DTO_1.clone())).getId();
        mvc.perform(put(FilmController.CONTROLLER_ROUTE + FilmController.LIKE_FILM_SUBROUTE, NON_EXISTING_ID, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get invalid films top")
    public void get_topFilm_badRequest400() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone());
        addFilm(VALID_FILM_DTO_2.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_TOP_FILMS_SUBROUTE)
                        .param(FilmController.COUNT_PARAM, COUNT_MINUS_ONE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get top 1 film (count provided)")
    public void get_top1Film_success200() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone());
        addFilm(VALID_FILM_DTO_2.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_TOP_FILMS_SUBROUTE)
                        .param(FilmController.COUNT_PARAM, COUNT_ONE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get top films (count not provided)")
    public void get_topFilms_success200() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone());
        addFilm(VALID_FILM_DTO_2.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_TOP_FILMS_SUBROUTE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get a specific film with valid id")
    public void get_film_validId_success200() throws Exception {
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_FILM_SUBROUTE, filmId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_FILM_DTO_1.getName()));
    }

    @Test
    @DisplayName("Get a specific film with invalid id")
    public void get_film_invalidId_notFound404() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_FILM_SUBROUTE, NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete a specific film with valid id")
    public void delete_film_validId_success200() throws Exception {
        Long filmId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone())).getId();
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.GET_FILM_SUBROUTE, filmId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_FILM_DTO_1.getName()));
    }

    @Test
    @DisplayName("Delete a specific film with invalid id")
    public void delete_film_invalidId_notFound404() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone());
        mvc.perform(delete(FilmController.CONTROLLER_ROUTE + FilmController.GET_FILM_SUBROUTE, NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add new film (valid film)")
    public void post_films_addValidFilm_success201WithDto() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone())
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(VALID_FILM_NAME_1));
        addFilm(VALID_FILM_DTO_2.clone())
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(VALID_FILM_NAME_2));
    }

    @Test
    @DisplayName("Add new film with incorrect field (null name)")
    public void post_films_addFilmWithNullName_fail400() throws Exception {
        addFilm(INVALID_FILM_DTO_NULL_NAME)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add already existing film")
    public void post_films_addExistingFilm_conflict409() throws Exception {
        FilmDto filmWithId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone()));

        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(filmWithId)
                        ))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Update existing film")
    public void put_films_updateExistingFilm_success200() throws Exception {
        FilmDto film1WithId = extractFilmDto(addFilm(VALID_FILM_DTO_1.clone()));
        film1WithId.setName(VALID_FILM_NAME_2);

        mvc.perform(put(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(film1WithId)
                        ))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(NAME_FIELD).value(VALID_FILM_NAME_2));
    }

    @Test
    @DisplayName("Update non-existing film")
    public void put_films_updateNonExistingFilm_notFound404() throws Exception {
        mvc.perform(put(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(VALID_FILM_DTO_1.clone())
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all films (not empty repository)")
    public void get_films_getFilmsFromNotEmptyRepository_success200WithDto() throws Exception {
        addFilm(VALID_FILM_DTO_1.clone());
        addFilm(VALID_FILM_DTO_2.clone());
        mvc.perform(get(FilmController.CONTROLLER_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get all films of a specific director")
    public void get_films_getDirectorFilms_success200WithCollectionOfDtos() throws Exception {
        addDirector(VALID_DIRECTOR_DTO_1.clone());
        addDirector(VALID_DIRECTOR_DTO_2.clone());
        addFilm(VALID_FILM_DTO_1.clone());
        addFilm(VALID_FILM_DTO_3.clone());

        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.GET_DIRECTOR_FILMS_SUBROUTE, VALID_DIRECTOR_DTO_1.getId()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Search films by substring in title and director returns matching films")
    public void films_searchByTitleAndDirectorSubstring_success200() throws Exception {
        DirectorDto director1 = VALID_DIRECTOR_DTO_1.clone();
        director1.setName(VALID_DIRECTOR_NAME_1);

        DirectorDto director2 = VALID_DIRECTOR_DTO_2.clone();
        director2.setName(VALID_DIRECTOR_NAME_2);

        DirectorDto director3 = VALID_DIRECTOR_DTO_3.clone();
        director3.setName(VALID_DIRECTOR_NAME_3);

        addDirector(director1);
        addDirector(director2);
        addDirector(director3);

        FilmDto film1 = VALID_FILM_DTO_5.clone();
        film1.setDirectors(Set.of(director2));

        FilmDto film2 = VALID_FILM_DTO_6.clone();
        film2.setDirectors(Set.of(director1));

        FilmDto film3 = VALID_FILM_DTO_7.clone();
        film3.setDirectors(Set.of(director3));

        FilmDto film4 = VALID_FILM_DTO_8.clone();
        film4.setDirectors(Set.of(director1));

        addFilm(film1);
        addFilm(film2);
        addFilm(film3);
        addFilm(film4);

        mvc.perform(get(FilmController.CONTROLLER_ROUTE + FilmController.FILMS_SEARCH_SUBROUTE)
                        .param(FilmController.QUERY_VAR, "крад")
                        .param(FilmController.BY_PARAM, "director,title"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }


    private ResultActions addFilm(FilmDto film) throws Exception {
        return mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(film)
                ));
    }

    private FilmDto extractFilmDto(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        String filmJson = actions
                .andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.readValue(filmJson, FilmDto.class);
    }

    private ResultActions addUser(UserDto user) throws Exception {
        return mvc.perform(post(UserController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(user)
                ));
    }

    private ResultActions addDirector(DirectorDto director) throws Exception {
        return mvc.perform(post(DirectorController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(director)
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
