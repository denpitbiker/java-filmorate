package ru.yandex.practicum.filmorate.presentation.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.yandex.practicum.filmorate.TestStubs.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Get a specific genre with valid id")
    public void get_genre_validId_success200() throws Exception {
        mvc.perform(get(GenreController.CONTROLLER_ROUTE + GenreController.GET_MPA_SUBROUTE, VALID_GENRE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_GENRE_VALUE));
    }

    @Test
    @DisplayName("Get a specific genre with invalid id")
    public void get_genre_invalidId_notFound404() throws Exception {
        mvc.perform(get(GenreController.CONTROLLER_ROUTE + GenreController.GET_MPA_SUBROUTE, NON_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all genres")
    public void get_genres_getMpasFromRepository_success200WithDto() throws Exception {
        mvc.perform(get(GenreController.CONTROLLER_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
