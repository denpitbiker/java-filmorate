package ru.yandex.practicum.filmorate.domain.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmServiceTest {
    private static final int TOP_ONE_COUNT = 1;
    private static final String NOT_FOUND_FILM_FAIL_MSG = "NotFoundException should be thrown for unknown film";

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Like a film")
    public void likeFilm_validLike_filmIsLiked() {
        FilmDto film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        UserDto user = userService.addUser(VALID_USER_DTO_1.clone());

        Assertions.assertDoesNotThrow(
                () -> filmService.likeFilm(film.getId(), user.getId()),
                "Liking the film should not throw exceptions"
        );

        FilmDto updatedFilm = filmService.getFilm(film.getId());
        Assertions.assertTrue(updatedFilm.getLikesIds().contains(user.getId()), "Film should contain the user's like");
    }

    @Test
    @DisplayName("Like existing film by non-existing user")
    public void likeFilm_likeExistingFilmByNonExistingUser_throwNotFoundException() {
        FilmDto film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.likeFilm(film.getId(), NON_EXISTING_ID),
                "NotFoundException should be thrown for unknown user"
        );
    }

    @Test
    @DisplayName("Like non-existing film by existing user")
    public void likeFilm_likeNonExistingFilmByExistingUser_throwNotFoundException() {
        UserDto user = userService.addUser(VALID_USER_DTO_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.likeFilm(NON_EXISTING_ID, user.getId()),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Unlike a film")
    public void unlikeFilm_validUnlike_filmIsUnliked() {
        FilmDto film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        UserDto user = userService.addUser(VALID_USER_DTO_1.clone());
        filmService.likeFilm(film.getId(), user.getId());

        Assertions.assertDoesNotThrow(
                () -> filmService.unlikeFilm(film.getId(), user.getId()),
                "Unliking the film should not throw exceptions"
        );

        FilmDto updatedFilm = filmService.getFilm(film.getId());
        Assertions.assertFalse(updatedFilm.getLikesIds().contains(user.getId()), "Film should not contain the user's like");
    }

    @Test
    @DisplayName("Unlike existing film by non-existing user")
    public void unlikeFilm_unlikeExistingFilmByNonExistingUser_throwNotFoundException() {
        FilmDto film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.unlikeFilm(film.getId(), NON_EXISTING_ID),
                "NotFoundException should be thrown for unknown user"
        );
    }

    @Test
    @DisplayName("Unlike non-existing film by existing user")
    public void unlikeFilm_unlikeNonExistingFilmByExistingUser_throwNotFoundException() {
        UserDto user = userService.addUser(VALID_USER_DTO_1.clone());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.unlikeFilm(NON_EXISTING_ID, user.getId()),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Get films top with negative count")
    public void getFilmsTop_negativeCount_throwsValidationException() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> filmService.getFilmsPopulars(-1),
                "Negative count should throw ValidationException"
        );
    }

    @Test
    @DisplayName("Get films top 1")
    public void getFilmsTop_getTop1Film_returnedAllFilms() {
        FilmDto film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        UserDto user = userService.addUser(VALID_USER_DTO_1.clone());
        filmService.likeFilm(film.getId(), user.getId());
        filmService.addFilm(VALID_FILM_DTO_2.clone());
        Assertions.assertDoesNotThrow(
                () -> filmService.getFilmsPopulars(TOP_ONE_COUNT),
                "Top 1 film should be returned without exceptions"
        );
        Collection<FilmDto> films = filmService.getFilmsPopulars(TOP_ONE_COUNT);
        Assertions.assertEquals(
                TOP_ONE_COUNT,
                films.size(),
                "Should return the correct number of top films"
        );
        Assertions.assertEquals(films.iterator().next().getId(), film.getId(), "Should return correct film");
    }

    @Test
    @DisplayName("Get popular films by genre only")
    public void getFilmsPopulars_byGenreOnly_returnsOnlyGenreFilms() {
        FilmDto film1 = VALID_FILM_DTO_1.clone();
        film1.setGenres(Set.of(genre(1L)));

        FilmDto film2 = VALID_FILM_DTO_2.clone();
        film2.setGenres(Set.of(genre(2L)));

        FilmDto addedFilm1 = filmService.addFilm(film1);
        FilmDto addedFilm2 = filmService.addFilm(film2);

        UserDto user1 = userService.addUser(VALID_USER_DTO_1.clone());
        UserDto user2 = userService.addUser(VALID_USER_DTO_2.clone());

        filmService.likeFilm(addedFilm1.getId(), user1.getId());
        filmService.likeFilm(addedFilm2.getId(), user2.getId());

        Collection<FilmDto> films = filmService.getFilmsPopulars(10, 1L, null);

        Assertions.assertEquals(1, films.size(), "Should return only one film of requested genre");
        Assertions.assertEquals(
                addedFilm1.getId(),
                films.iterator().next().getId(),
                "Should return film with requested genre"
        );
    }

    @Test
    @DisplayName("Get popular films by genre and year")
    public void getFilmsPopulars_byGenreAndYear_returnsOnlyMatchedFilms() {
        FilmDto film1 = VALID_FILM_DTO_1.clone();
        film1.setGenres(Set.of(genre(1L)));
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));

        FilmDto film2 = VALID_FILM_DTO_2.clone();
        film2.setGenres(Set.of(genre(1L)));
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));

        FilmDto addedFilm1 = filmService.addFilm(film1);
        FilmDto addedFilm2 = filmService.addFilm(film2);

        UserDto user1 = userService.addUser(VALID_USER_DTO_1.clone());
        UserDto user2 = userService.addUser(VALID_USER_DTO_2.clone());

        filmService.likeFilm(addedFilm1.getId(), user1.getId());
        filmService.likeFilm(addedFilm2.getId(), user2.getId());

        Collection<FilmDto> films = filmService.getFilmsPopulars(10, 1L, 2020);

        Assertions.assertEquals(1, films.size(), "Should return only one film of requested genre and year");
        Assertions.assertEquals(
                addedFilm1.getId(),
                films.iterator().next().getId(),
                "Should return film with requested genre and year"
        );
    }

    @Test
    @DisplayName("Get popular films by year only")
    public void getFilmsPopulars_byYearOnly_returnsOnlyYearFilms() {
        FilmDto film1 = VALID_FILM_DTO_1.clone();
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));

        FilmDto film2 = VALID_FILM_DTO_2.clone();
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));

        FilmDto addedFilm1 = filmService.addFilm(film1);
        FilmDto addedFilm2 = filmService.addFilm(film2);

        UserDto user1 = userService.addUser(VALID_USER_DTO_1.clone());
        UserDto user2 = userService.addUser(VALID_USER_DTO_2.clone());

        filmService.likeFilm(addedFilm1.getId(), user1.getId());
        filmService.likeFilm(addedFilm2.getId(), user2.getId());

        Collection<FilmDto> films = filmService.getFilmsPopulars(10, null, 2020);

        Assertions.assertEquals(1, films.size(), "Should return only one film of requested year");
        Assertions.assertEquals(
                addedFilm1.getId(),
                films.iterator().next().getId(),
                "Should return film with requested year"
        );
    }

    @Test
    @DisplayName("Get all films")
    public void getAllFilms_getAllFilms_returnedAllFilms() {
        filmService.addFilm(VALID_FILM_DTO_1.clone());
        filmService.addFilm(VALID_FILM_DTO_2.clone());
        Assertions.assertDoesNotThrow(
                () -> filmService.getAllFilms(),
                "Films should be returned without exceptions"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_TWO,
                filmService.getAllFilms().size(),
                "Should be exact two films"
        );
    }

    @Test
    @DisplayName("Get film by id")
    public void getFilm_getExistingFilmById_returnedFilm() {
        FilmDto added = filmService.addFilm(VALID_FILM_DTO_2.clone());
        Assertions.assertDoesNotThrow(
                () -> filmService.getFilm(added.getId()),
                "Film should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Get non-existing film")
    public void getFilm_getNonExistingFilm_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.getFilm(NON_EXISTING_ID),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Delete film by id")
    public void getFilm_deleteExistingFilmById_returnedFilm() {
        FilmDto added = filmService.addFilm(VALID_FILM_DTO_2.clone());
        Assertions.assertDoesNotThrow(
                () -> filmService.deleteFilm(added.getId()),
                "Film should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Delete non-existing film")
    public void deleteFilm_deleteNonExistingFilm_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.deleteFilm(NON_EXISTING_ID),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Add film")
    public void addFilm_addNewFilm_filmAddedNoExceptions() {
        Assertions.assertDoesNotThrow(
                () -> filmService.addFilm(VALID_FILM_DTO_1.clone()),
                "Film should be added without exceptions"
        );
    }

    @Test
    @DisplayName("Add duplicate film")
    public void addFilm_addExistingFilm_throwDuplicatedDataException() {
        FilmDto film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        Assertions.assertThrows(
                DuplicatedDataException.class,
                () -> filmService.addFilm(film),
                "DuplicatedDataException should be thrown for duplicate film"
        );
    }

    @Test
    @DisplayName("Update film")
    public void updateFilm_updateExistingFilm_filmUpdatedNoExceptions() {
        FilmDto filmToUpdate = filmService.addFilm(VALID_FILM_DTO_2.clone());
        filmToUpdate.setDescription(VALID_FILM_DESCRIPTION_2);
        Assertions.assertDoesNotThrow(
                () -> filmService.updateFilm(filmToUpdate),
                "Film should be updated without exceptions"
        );
    }

    @Test
    @DisplayName("Update non-existing film")
    public void updateFilm_updateNonExistingFilm_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(VALID_FILM_DTO_1.clone()),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    private GenreDto genre(Long id) {
        return new GenreDto(id, "test");
    }
}
