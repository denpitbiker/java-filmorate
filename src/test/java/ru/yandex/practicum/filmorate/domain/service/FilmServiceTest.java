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
import ru.yandex.practicum.filmorate.data.model.enums.SortBy;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.presentation.dto.DirectorDto;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    @Autowired
    private DirectorService directorService;

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
                () -> filmService.getFilmsPopulars(-1, null, null),
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
                () -> filmService.getFilmsPopulars(TOP_ONE_COUNT, null, null),
                "Top 1 film should be returned without exceptions"
        );
        Collection<FilmDto> films = filmService.getFilmsPopulars(TOP_ONE_COUNT, null, null);
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

    @Test
    @DisplayName("Get films of an existing director")
    public void getDirectorFilms_getFilmsOfExistingDirector_filmsReturned() {
        directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());
        directorService.addDirector(VALID_DIRECTOR_DTO_2.clone());
        filmService.addFilm(VALID_FILM_DTO_1.clone());
        FilmDto filmWithDirector = filmService.addFilm(VALID_FILM_DTO_3.clone());

        Collection<FilmDto> films = filmService.getDirectorFilms(VALID_DIRECTOR_DTO_1.getId(), SortBy.YEAR);
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_ONE, films.size());
        Assertions.assertEquals(filmWithDirector.getId(), films.iterator().next().getId());
    }

    @Test
    @DisplayName("Get films of an existing director by year")
    public void getDirectorFilms_getFilmsOfExistingDirector_filmsReturnedInYearOrder() {
        directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());
        directorService.addDirector(VALID_DIRECTOR_DTO_2.clone());
        FilmDto filmWithDirector1 = filmService.addFilm(VALID_FILM_DTO_3.clone());
        FilmDto filmWithDirector2 = filmService.addFilm(VALID_FILM_DTO_4.clone());

        List<FilmDto> films = new ArrayList<>(filmService.getDirectorFilms(VALID_DIRECTOR_DTO_2.getId(), SortBy.YEAR));
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, films.size());
        Assertions.assertEquals(filmWithDirector1.getId(), films.getFirst().getId());
        Assertions.assertEquals(filmWithDirector2.getId(), films.get(1).getId());
    }

    @Test
    @DisplayName("Get films of an existing director by likes")
    public void getDirectorFilms_getFilmsOfExistingDirector_filmsReturnedInLikesOrder() {
        UserDto user = userService.addUser(VALID_USER_DTO_1);
        directorService.addDirector(VALID_DIRECTOR_DTO_1.clone());
        directorService.addDirector(VALID_DIRECTOR_DTO_2.clone());
        FilmDto filmWithDirector1 = filmService.addFilm(VALID_FILM_DTO_3.clone());
        FilmDto filmWithDirector2 = filmService.addFilm(VALID_FILM_DTO_4.clone());
        filmService.likeFilm(user.getId(), filmWithDirector1.getId());

        List<FilmDto> films = new ArrayList<>(filmService.getDirectorFilms(VALID_DIRECTOR_DTO_2.getId(), SortBy.LIKES));
        Assertions.assertEquals(EXPECTED_REPOSITORY_SIZE_TWO, films.size());
        Assertions.assertEquals(filmWithDirector1.getId(), films.getFirst().getId());
        Assertions.assertEquals(filmWithDirector2.getId(), films.get(1).getId());
    }

    @Test
    @DisplayName("Get films of a non-existing director")
    public void getDirectorFilms_getFilmsOfNonExistingDirector_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> filmService.getDirectorFilms(VALID_DIRECTOR_DTO_1.getId(), SortBy.YEAR)
        );
    }

    @Test
    @DisplayName("Search films by substring in title and director")
    public void searchFilms_searchByTitleAndDirectorSubstring_filmsReturned() {
        DirectorDto director1 = VALID_DIRECTOR_DTO_1.clone();
        director1.setName(VALID_DIRECTOR_NAME_1);
        DirectorDto addedDirector1 = directorService.addDirector(director1);

        DirectorDto director2 = VALID_DIRECTOR_DTO_2.clone();
        director2.setName(VALID_DIRECTOR_NAME_2);
        DirectorDto addedDirector2 = directorService.addDirector(director2);

        DirectorDto director3 = VALID_DIRECTOR_DTO_3.clone();
        director3.setName(VALID_DIRECTOR_NAME_3);
        DirectorDto addedDirector3 = directorService.addDirector(director3);

        FilmDto film1 = VALID_FILM_DTO_5.clone();
        film1.setDirectors(Set.of(addedDirector2));

        FilmDto film2 = VALID_FILM_DTO_6.clone();
        film2.setDirectors(Set.of(addedDirector1));

        FilmDto film3 = VALID_FILM_DTO_7.clone();
        film3.setDirectors(Set.of(addedDirector3));

        FilmDto film4 = VALID_FILM_DTO_8.clone();
        film4.setDirectors(Set.of(addedDirector1));

        filmService.addFilm(film1);
        filmService.addFilm(film2);
        filmService.addFilm(film3);
        filmService.addFilm(film4);

        Collection<FilmDto> films = filmService.searchFilms("крад", "director,title");

        Assertions.assertEquals(3, films.size(), "Should return three matching films");
    }

    @Test
    @DisplayName("Search films with no matches returns empty collection")
    public void searchFilms_noMatches_returnsEmptyCollection() {
        DirectorDto director1 = VALID_DIRECTOR_DTO_1.clone();
        director1.setName(VALID_DIRECTOR_NAME_1);
        DirectorDto addedDirector1 = directorService.addDirector(director1);

        DirectorDto director2 = VALID_DIRECTOR_DTO_2.clone();
        director2.setName(VALID_DIRECTOR_NAME_2);
        DirectorDto addedDirector2 = directorService.addDirector(director2);

        FilmDto film1 = VALID_FILM_DTO_1.clone();
        film1.setDirectors(Set.of(addedDirector1));

        FilmDto film2 = VALID_FILM_DTO_2.clone();
        film2.setDirectors(Set.of(addedDirector2));

        filmService.addFilm(film1);
        filmService.addFilm(film2);

        Collection<FilmDto> films = filmService.searchFilms("крад", "director,title");

        Assertions.assertEquals(0, films.size(), "Should return no films");
    }

    @Test
    @DisplayName("Search films by director only does not return films matched by title")
    public void searchFilms_searchByDirectorOnly_noMatchesReturned() {
        DirectorDto director1 = VALID_DIRECTOR_DTO_1.clone();
        director1.setName(VALID_DIRECTOR_NAME_1);
        DirectorDto addedDirector1 = directorService.addDirector(director1);

        DirectorDto director2 = VALID_DIRECTOR_DTO_2.clone();
        director2.setName(VALID_DIRECTOR_NAME_2);
        DirectorDto addedDirector2 = directorService.addDirector(director2);

        FilmDto film1 = VALID_FILM_DTO_5.clone();
        film1.setDirectors(Set.of(addedDirector2));

        FilmDto film2 = VALID_FILM_DTO_6.clone();
        film2.setDirectors(Set.of(addedDirector1));

        filmService.addFilm(film1);
        filmService.addFilm(film2);

        Collection<FilmDto> films = filmService.searchFilms("крад", "director");

        Assertions.assertEquals(0, films.size(), "Should return no films when only director search is used");
    }

    @Test
    @DisplayName("Search films by title only does not return films matched by director")
    public void searchFilms_searchByTitleOnly_noMatchesReturned() {
        DirectorDto director = VALID_DIRECTOR_DTO_3.clone();
        DirectorDto addedDirector = directorService.addDirector(director);

        FilmDto film1 = VALID_FILM_DTO_1.clone();
        film1.setDirectors(Set.of(addedDirector));

        FilmDto film2 = VALID_FILM_DTO_2.clone();
        film2.setDirectors(Set.of(addedDirector));

        filmService.addFilm(film1);
        filmService.addFilm(film2);

        Collection<FilmDto> films = filmService.searchFilms("крад", "title");

        Assertions.assertEquals(0, films.size(), "Should return no films when only title search is used");
    }

    @Test
    @DisplayName("Search films by director only returns films matched by director")
    public void searchFilms_searchByDirectorOnly_oneMatchReturned() {
        DirectorDto director1 = VALID_DIRECTOR_DTO_1.clone();
        director1.setName(VALID_DIRECTOR_NAME_1);
        DirectorDto addedDirector1 = directorService.addDirector(director1);

        DirectorDto director3 = VALID_DIRECTOR_DTO_3.clone();
        DirectorDto addedDirector3 = directorService.addDirector(director3);

        FilmDto film1 = VALID_FILM_DTO_5.clone();
        film1.setDirectors(Set.of(addedDirector1));

        FilmDto film2 = VALID_FILM_DTO_6.clone();
        film2.setDirectors(Set.of(addedDirector3));

        film1 = filmService.addFilm(film1);
        film2 = filmService.addFilm(film2);

        Collection<FilmDto> films = filmService.searchFilms("крад", "director");

        Assertions.assertEquals(1, films.size());
        Assertions.assertEquals(film2.getId(), films.iterator().next().getId());
    }

    @Test
    @DisplayName("Search films by title only returns films matched by title")
    public void searchFilms_searchByTitleOnly_oneMatchReturned() {
        DirectorDto director3 = VALID_DIRECTOR_DTO_3.clone();
        DirectorDto addedDirector3 = directorService.addDirector(director3);

        DirectorDto director2 = VALID_DIRECTOR_DTO_2.clone();
        director2.setName(VALID_DIRECTOR_NAME_2);
        DirectorDto addedDirector2 = directorService.addDirector(director2);

        FilmDto film1 = VALID_FILM_DTO_5.clone();
        film1.setDirectors(Set.of(addedDirector2));

        FilmDto film2 = VALID_FILM_DTO_1.clone();
        film2.setDirectors(Set.of(addedDirector3));

        film1 = filmService.addFilm(film1);
        film2 = filmService.addFilm(film2);

        Collection<FilmDto> films = filmService.searchFilms("крад", "title");

        Assertions.assertEquals(1, films.size());
        Assertions.assertEquals(film1.getId(), films.iterator().next().getId());
    }
}
