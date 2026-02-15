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

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Add new film (valid film)")
    public void POST_film_validParams_Success201WithDto() throws Exception {
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
    @DisplayName("Add new film (null name)")
    public void POST_film_null_name_Fail400() throws Exception {
        mvc.perform(post(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.INVALID_FILM_NULL_NAME.clone())
                        ))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add already existing film")
    public void POST_film_exists_Conflict409() throws Exception {
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
    public void PUT_film_exists_Success200() throws Exception {
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
    public void PUT_film_exists_NotFound404() throws Exception {
        mvc.perform(put(FilmController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(TestStubs.VALID_FILM_2.clone())
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all films (not empty repository)")
    public void GET_films_not_empty_repository_Success200WithDto() throws Exception {
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
