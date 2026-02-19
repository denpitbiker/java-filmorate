package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class FilmRepositoryTest {
    private static final int EXPECTED_REPOSITORY_SIZE_TWO = 2;

    @Autowired
    private FilmRepository repository;

    @Test
    @DisplayName("Add correct film")
    public void addFilm_addCorrectFilm_filmAddedNoExceptions() {
        Assertions.assertDoesNotThrow(
                () -> repository.addFilm(VALID_FILM_1.clone()),
                "Film should be added without exceptions"
        );
        Assertions.assertDoesNotThrow(
                () -> repository.addFilm(VALID_FILM_2.clone()),
                "Second film should be added without exceptions"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllFilms().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }

    @Test
    @DisplayName("Add existing film")
    public void addFilm_addExistingFilm_throwDuplicateException() {
        Film addedFilm = repository.addFilm(VALID_FILM_1.clone());

        Assertions.assertThrows(DuplicatedDataException.class,
                () -> repository.addFilm(addedFilm),
                "Duplicate films are prohibited"
        );
    }

    @Test
    @DisplayName("Update non-existing film")
    public void updateFilm_updateNonExistingFilm_throwNotFoundException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> repository.updateFilm(VALID_FILM_1.clone()),
                "Can't update non-existing film!"
        );
    }

    @Test
    @DisplayName("Update existing film")
    public void updateFilm_updateExistingFilm_filmUpdated() {
        Film addedFilm = repository.addFilm(VALID_FILM_1.clone());
        addedFilm.setName(VALID_FILM_NAME_2);
        Assertions.assertDoesNotThrow(
                () -> repository.updateFilm(addedFilm),
                "Film should be updated"
        );
    }

    @Test
    @DisplayName("Get list of all films")
    public void getFilms_getAllFilms_listOfFilms() {
        repository.addFilm(VALID_FILM_1.clone());
        repository.addFilm(VALID_FILM_2.clone());

        Assertions.assertDoesNotThrow(
                () -> repository.getAllFilms(),
                "Films should be returned without exceptions"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllFilms().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }
}
