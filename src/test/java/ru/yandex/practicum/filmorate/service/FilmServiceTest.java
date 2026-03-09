package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.TestStubs;
import ru.yandex.practicum.filmorate.domain.service.FilmService;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.data.storage.impl.inmemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.inmemory.InMemoryUserStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmServiceTest {
    private static final int TOP_ONE_COUNT = 1;
    private static final String NOT_FOUND_FILM_FAIL_MSG = "NotFoundException should be thrown for unknown film";

    @Autowired
    private InMemoryFilmStorage filmStorage;

    @Autowired
    private InMemoryUserStorage userStorage;

    @Autowired
    private FilmService service;

    @Test
    @DisplayName("Like a film")
    public void likeFilm_validLike_filmIsLiked() {
        Film film = filmStorage.addFilm(VALID_FILM_1.clone());
        User user = userStorage.addUser(VALID_USER_1.clone());

        Assertions.assertDoesNotThrow(
                () -> service.likeFilm(film.id(), user.id()),
                "Liking the film should not throw exceptions"
        );

        Film updatedFilm = service.getFilm(film.id());
        Assertions.assertTrue(updatedFilm.likesIds().contains(user.id()), "Film should contain the user's like");
    }

    @Test
    @DisplayName("Like existing film by non-existing user")
    public void likeFilm_likeExistingFilmByNonExistingUser_throwNotFoundException() {
        Film film = filmStorage.addFilm(VALID_FILM_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.likeFilm(film.id(), NON_EXISTING_ID),
                "NotFoundException should be thrown for unknown user"
        );
    }

    @Test
    @DisplayName("Like non-existing film by existing user")
    public void likeFilm_likeNonExistingFilmByExistingUser_throwNotFoundException() {
        User user = userStorage.addUser(VALID_USER_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.likeFilm(NON_EXISTING_ID, user.id()),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Unlike a film")
    public void unlikeFilm_validUnlike_filmIsUnliked() {
        Film film = filmStorage.addFilm(VALID_FILM_1.clone());
        User user = userStorage.addUser(VALID_USER_1.clone());
        service.likeFilm(film.id(), user.id());

        Assertions.assertDoesNotThrow(
                () -> service.unlikeFilm(film.id(), user.id()),
                "Unliking the film should not throw exceptions"
        );

        Film updatedFilm = service.getFilm(film.id());
        Assertions.assertFalse(updatedFilm.likesIds().contains(user.id()), "Film should not contain the user's like");
    }

    @Test
    @DisplayName("Unlike existing film by non-existing user")
    public void unlikeFilm_unlikeExistingFilmByNonExistingUser_throwNotFoundException() {
        Film film = filmStorage.addFilm(VALID_FILM_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.unlikeFilm(film.id(), NON_EXISTING_ID),
                "NotFoundException should be thrown for unknown user"
        );
    }

    @Test
    @DisplayName("Unlike non-existing film by existing user")
    public void unlikeFilm_unlikeNonExistingFilmByExistingUser_throwNotFoundException() {
        User user = userStorage.addUser(VALID_USER_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.unlikeFilm(NON_EXISTING_ID, user.id()),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Get films top with negative count")
    public void getFilmsTop_negativeCount_throwsValidationException() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> service.getFilmsTop(-1),
                "Negative count should throw ValidationException"
        );
    }

    @Test
    @DisplayName("Get films top 1")
    public void getFilmsTop_getTop1Film_returnedAllFilms() {
        Film film = filmStorage.addFilm(VALID_FILM_1.clone());
        User user = userStorage.addUser(VALID_USER_1.clone());
        service.likeFilm(film.id(), user.id());
        filmStorage.addFilm(TestStubs.VALID_FILM_2.clone());
        Assertions.assertDoesNotThrow(
                () -> service.getFilmsTop(TOP_ONE_COUNT),
                "Top 1 film should be returned without exceptions"
        );
        Collection<Film> films = service.getFilmsTop(TOP_ONE_COUNT);
        Assertions.assertEquals(
                TOP_ONE_COUNT,
                films.size(),
                "Should return the correct number of top films"
        );
        Assertions.assertTrue(
                films.iterator().next().id().equals(film.id()),
                "Should return correct film"
        );
    }

    @Test
    @DisplayName("Get all films")
    public void getAllFilms_getAllFilms_returnedAllFilms() {
        filmStorage.addFilm(VALID_FILM_1.clone());
        filmStorage.addFilm(VALID_FILM_2.clone());
        Assertions.assertDoesNotThrow(
                () -> service.getAllFilms(),
                "Films should be returned without exceptions"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_TWO,
                service.getAllFilms().size(),
                "Should be exact two films"
        );
    }

    @Test
    @DisplayName("Get film by id")
    public void getFilm_getExistingFilmById_returnedFilm() {
        Film added = filmStorage.addFilm(VALID_FILM_1.clone());
        Assertions.assertDoesNotThrow(
                () -> service.getFilm(added.id()),
                "Film should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Get non-existing film")
    public void getFilm_getNonExistingFilm_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.getFilm(NON_EXISTING_ID),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Add film")
    public void addFilm_addNewFilm_filmAddedNoExceptions() {
        Assertions.assertDoesNotThrow(
                () -> service.addFilm(VALID_FILM_1.clone()),
                "Film should be added without exceptions"
        );
    }

    @Test
    @DisplayName("Add duplicate film")
    public void addFilm_addExistingFilm_throwDuplicatedDataException() {
        Film film = service.addFilm(VALID_FILM_1.clone());
        Assertions.assertThrows(
                DuplicatedDataException.class,
                () -> service.addFilm(film),
                "DuplicatedDataException should be thrown for duplicate film"
        );
    }

    @Test
    @DisplayName("Update film")
    public void updateFilm_updateExistingFilm_filmUpdatedNoExceptions() {
        Film filmToUpdate = filmStorage.addFilm(VALID_FILM_1.clone());
        filmToUpdate.setDescription(VALID_FILM_DESCRIPTION_2);
        Assertions.assertDoesNotThrow(
                () -> service.updateFilm(filmToUpdate),
                "Film should be updated without exceptions"
        );
    }

    @Test
    @DisplayName("Update non-existing film")
    public void updateFilm_updateNonExistingFilm_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> service.updateFilm(VALID_FILM_1.clone()),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }
}
