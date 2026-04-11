package ru.yandex.practicum.filmorate.data.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.data.model.enums.SearchCondition;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;

import java.util.Set;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public abstract class FilmStorageTest {

    final FilmStorage repository;

    protected FilmStorageTest(FilmStorage repository) {
        this.repository = repository;
    }

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
                repository::getAllFilms,
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

    @Test
    @DisplayName("Search films by substring returns all matching films")
    public void searchFilms_searchBySubstring_returnsMatchingFilms() {
        Film film1 = VALID_FILM_1.clone();
        film1.setName(VALID_FILM_NAME_4);

        Film film2 = VALID_FILM_2.clone();
        film2.setName(VALID_FILM_NAME_5);

        Film film3 = VALID_FILM_1.clone();
        film3.setName(VALID_FILM_NAME_6);

        Film film4 = VALID_FILM_1.clone();
        film4.setName(VALID_FILM_NAME_7);

        repository.addFilm(film1);
        repository.addFilm(film2);
        repository.addFilm(film3);
        repository.addFilm(film4);

        var result = repository.searchFilms("крад", Set.of(SearchCondition.TITLE));

        Assertions.assertEquals(2, result.size(), "Two films should match substring 'крад'");

        var names = result.stream()
                .map(Film::getName)
                .toList();

        Assertions.assertTrue(
                names.contains(VALID_FILM_NAME_4),
                "First matching film should be returned"
        );

        Assertions.assertTrue(
                names.contains(VALID_FILM_NAME_5),
                "Second matching film should be returned"
        );
    }
}
