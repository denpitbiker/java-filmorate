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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmServiceTest {
    private static final int TOP_ONE_COUNT = 1;

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
                () -> service.likeFilm(film.getId(), user.getId()),
                "Liking the film should not throw exceptions"
        );

        Film updatedFilm = service.getFilm(film.getId());
        Assertions.assertTrue(updatedFilm.getLikes().contains(user.getId()), "Film should contain the user's like");
    }

    @Test
    @DisplayName("Unlike a film")
    public void unlikeFilm_validUnlike_filmIsUnliked() {
        Film film = filmStorage.addFilm(VALID_FILM_1.clone());
        User user = userStorage.addUser(VALID_USER_1.clone());
        service.likeFilm(film.getId(), user.getId());

        Assertions.assertDoesNotThrow(
                () -> service.unlikeFilm(film.getId(), user.getId()),
                "Unliking the film should not throw exceptions"
        );

        Film updatedFilm = service.getFilm(film.getId());
        Assertions.assertFalse(updatedFilm.getLikes().contains(user.getId()), "Film should not contain the user's like");
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
        service.likeFilm(film.getId(), user.getId());
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
                films.iterator().next().getId().equals(film.getId()),
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
                () -> service.getFilm(added.getId()),
                "Film should be returned without exceptions"
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
    @DisplayName("Update film")
    public void updateFilm_updateExistingFilm_filmUpdatedNoExceptions() {
        Film filmToUpdate = filmStorage.addFilm(VALID_FILM_1.clone());
        filmToUpdate.setDescription(VALID_FILM_DESCRIPTION_2);
        Assertions.assertDoesNotThrow(
                () -> service.updateFilm(filmToUpdate),
                "Film should be updated without exceptions"
        );
    }
}
