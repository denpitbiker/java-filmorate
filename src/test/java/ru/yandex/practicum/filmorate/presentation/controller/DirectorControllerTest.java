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

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class DirectorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Get all directors")
    public void get_allDirectors_success200() throws Exception {
        mvc.perform(get(DirectorController.CONTROLLER_ROUTE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Get director by id")
    public void get_oneExistingDirectorById_success200() throws Exception {
        Long directorId = extractDirectorDto(addDirector(VALID_DIRECTOR_DTO_1.clone())).getId();
        mvc.perform(get(DirectorController.CONTROLLER_ROUTE + DirectorController.GET_DIRECTOR_SUBROUTE, directorId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_DIRECTOR_DTO_1.getName()));
    }

    @Test
    @DisplayName("Get non-existing director by id")
    public void get_oneNonExistingDirectorById_notFound404() throws Exception {
        mvc.perform(get(DirectorController.CONTROLLER_ROUTE + DirectorController.GET_DIRECTOR_SUBROUTE, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Add director")
    public void post_addDirector_created201() throws Exception {
        addDirector(VALID_DIRECTOR_DTO_1.clone())
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_DIRECTOR_NAME_1));
        addDirector(VALID_DIRECTOR_DTO_2.clone())
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_DIRECTOR_NAME_2));
    }

    @Test
    @DisplayName("Add incorrect director")
    public void post_addDirectorWithNullName_fail400() throws Exception {
        addDirector(INVALID_DIRECTOR_DTO)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update existing director")
    public void put_updateExistingDirector_success200() throws Exception {
        DirectorDto director1 = extractDirectorDto(addDirector(VALID_DIRECTOR_DTO_1.clone()));
        director1.setName(VALID_DIRECTOR_NAME_2);

        mvc.perform(put(DirectorController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(director1)
                        ))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_DIRECTOR_NAME_2));
    }

    @Test
    @DisplayName("Update non-existing director")
    public void put_updateNonExistingDirector_notFound404() throws Exception {
        DirectorDto nonExistingDirector = VALID_DIRECTOR_DTO_1.clone();
        nonExistingDirector.setId(1000L);

        mvc.perform(put(DirectorController.CONTROLLER_ROUTE)
                        .contentType(MediaType.APPLICATION_JSON).content(
                                asJsonString(nonExistingDirector.clone())
                        ))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete existing director")
    public void delete_deleteExistingDirector_success200() throws Exception {
        Long director = extractDirectorDto(addDirector(VALID_DIRECTOR_DTO_1.clone())).getId();
        mvc.perform(delete(DirectorController.CONTROLLER_ROUTE + DirectorController.GET_DIRECTOR_SUBROUTE, director))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(VALID_DIRECTOR_DTO_1.getName()));
    }

    @Test
    @DisplayName("Delete non-existing director")
    public void delete_deleteNonExistingDirector_notFoundException() throws Exception {
        addDirector(VALID_DIRECTOR_DTO_1.clone());
        mvc.perform(delete(DirectorController.CONTROLLER_ROUTE + DirectorController.GET_DIRECTOR_SUBROUTE, 1000L))
                .andExpect(status().isNotFound());
    }

    private ResultActions addDirector(DirectorDto director) throws Exception {
        return mvc.perform(post(DirectorController.CONTROLLER_ROUTE)
                .contentType(MediaType.APPLICATION_JSON).content(
                        asJsonString(director)
                ));
    }

    private DirectorDto extractDirectorDto(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        String directorJson = actions
                .andReturn().getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.readValue(directorJson, DirectorDto.class);
    }
}
