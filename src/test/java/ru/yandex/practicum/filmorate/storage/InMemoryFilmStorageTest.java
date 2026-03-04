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
    public void addFilm_addCorrectFilm_filmAddedAndReturned() {
        Assertions.assertNotNull(
                repository.addFilm(VALID_FILM_1.clone()),
                "Film should be added and returned"
        );
        Assertions.assertNotNull(
                repository.addFilm(VALID_FILM_2.clone()),
                "Second film should be added and returned"
        );
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, repository.getAllFilms().size(), "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO);
    }

    @Test
    @DisplayName("Get existing film by id")
    public void getFilm_getFilmByExistingId_filmReturned() {
        Film film = repository.addFilm(VALID_FILM_1.clone());

        Assertions.assertTrue(
                () -> repository.getFilm(film.getId()).isPresent(),
                "Film should be returned for existing id"
        );
    }

    @Test
    @DisplayName("Remove existing film by id")
    public void removeFilm_removeFilmByExistingId_filmRemovedAndReturned() {
        Film film = repository.addFilm(VALID_FILM_1.clone());

        Assertions.assertNotNull(
                repository.removeFilm(film.getId()),
                "Film should be returned for existing id"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                repository.getAllFilms().size(),
                "Film should be removed"
        );
    }

    @Test
    @DisplayName("Update existing film")
    public void updateFilm_updateExistingFilm_filmUpdated() {
        Film addedFilm = repository.addFilm(VALID_FILM_1.clone());
        addedFilm.setName(VALID_FILM_NAME_2);
        Assertions.assertNotNull(
                repository.updateFilm(addedFilm),
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

    @Test
    @DisplayName("Check existing film id")
    public void hasFilmId_checkExistingFilmIdInStorage_returnedTrue() {
        Long id = repository.addFilm(VALID_FILM_1.clone()).getId();
        Assertions.assertTrue(repository.hasFilmId(id), "Repository should contain added film id");
    }

    @Test
    @DisplayName("Check non-existing film id")
    public void hasFilmId_checkNonExistingFilmIdInStorage_returnedFalse() {
        Assertions.assertFalse(repository.hasFilmId(NON_EXISTING_ID), "Repository should not contain unknown film id");
    }
}
