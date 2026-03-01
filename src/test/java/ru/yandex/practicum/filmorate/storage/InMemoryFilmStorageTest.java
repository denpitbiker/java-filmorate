package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class InMemoryFilmStorageTest {

    @Autowired
    private InMemoryFilmStorage repository;

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
    @DisplayName("Get existing film by id")
    public void getFilm_getFilmByExistingId_filmReturnedNoExceptions() {
        Film film = repository.addFilm(VALID_FILM_1.clone());

        Assertions.assertDoesNotThrow(
                () -> repository.getFilm(film.getId()),
                "Film should be returned for existing id"
        );
    }

    @Test
    @DisplayName("Get non-existing film by id")
    public void getFilm_getFilmByNonExistingId_NotFoundException() {
        repository.addFilm(VALID_FILM_1.clone());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> repository.getFilm(NON_EXISTING_ID),
                "Should not return film for non-existing id"
        );
    }

    @Test
    @DisplayName("Remove existing film by id")
    public void removeFilm_removeFilmByExistingId_filmRemovedAndReturnedNoExceptions() {
        Film film = repository.addFilm(VALID_FILM_1.clone());

        Assertions.assertDoesNotThrow(
                () -> repository.removeFilm(film.getId()),
                "Film should be returned for existing id"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                repository.getAllFilms().size(),
                "Film should be removed"
        );
    }

    @Test
    @DisplayName("Remove non-existing film by id")
    public void removeFilm_getFilmByNonExistingId_NotFoundException() {
        repository.addFilm(VALID_FILM_1.clone());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> repository.removeFilm(NON_EXISTING_ID),
                "Should not return film for non-existing id"
        );

        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ONE,
                repository.getAllFilms().size(),
                "Other film should not be removed"
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
