package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.tool.comparison.FilmLikesComparator;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final String GET_FILM_LOG_MSG = "Get film {}";
    private static final String LIKE_FILM_LOG_MSG = "Is success like film {} by user {}: {}";
    private static final String UNLIKE_FILM_LOG_MSG = "Is success unlike film {} by user {}: {}";
    private static final String GET_TOP_FILMS_LOG_MSG = "Get top {} films";
    private static final String GET_FILMS_LOG_MSG = "Get all films";
    private static final String ADD_FILM_LOG_MSG = "Add new film {}";
    private static final String UPDATE_FILM_LOG_MSG = "Update film {}";
    private static final String USER_NOT_FOUND_TRACE_MSG = "Can't find user with id: {}";
    private static final String DUPLICATE_FILM_FOUND_TRACE_MSG = "Already have film with id: {}";
    private static final String FILM_NOT_FOUND_TRACE_MSG = "Can't find film with id: {}";

    private static final String USER_NOT_FOUND_ERR_MSG = "Can't find user with id = ";
    private static final String DUPLICATE_FILM_ERR_MSG = "Film already exists with id = ";
    private static final String FILM_NOT_FOUND_ERR_MSG = "Can't find film with id = ";
    private static final String FILMS_COUNT_ERR_MSG = "Films count must be positive number!";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesComparator filmLikesComparator = new FilmLikesComparator(true);

    public void likeFilm(Long id, Long userId) {
        checkFilmIdExist(id);
        Film film = filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id));
        checkUserIdExist(userId);
        boolean isSuccess = film.getLikes().add(userId);
        log.info(LIKE_FILM_LOG_MSG, id, userId, isSuccess);
        filmStorage.updateFilm(film);
    }

    public void unlikeFilm(Long id, Long userId) {
        checkFilmIdExist(id);
        Film film = filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id));
        checkUserIdExist(userId);
        boolean isSuccess = film.getLikes().remove(userId);
        log.info(UNLIKE_FILM_LOG_MSG, id, userId, isSuccess);
        filmStorage.updateFilm(film);
    }

    public Collection<Film> getFilmsTop(Integer count) {
        log.info(GET_TOP_FILMS_LOG_MSG, count);
        if (count <= 0) throw new ValidationException(FILMS_COUNT_ERR_MSG);
        return filmStorage.getAllFilms().stream()
                .sorted(filmLikesComparator)
                .limit(count)
                .toList();
    }

    public Film getFilm(Long id) {
        log.info(GET_FILM_LOG_MSG, id);
        checkFilmIdExist(id);
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id));
    }

    public Collection<Film> getAllFilms() {
        log.info(GET_FILMS_LOG_MSG);
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film newFilm) {
        log.info(ADD_FILM_LOG_MSG, newFilm);
        checkFilmIdNotExist(newFilm.getId());
        return filmStorage.addFilm(newFilm)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_ERR_MSG + newFilm.getId()));
    }

    public Film updateFilm(Film updatedFilm) {
        log.info(UPDATE_FILM_LOG_MSG, updatedFilm);
        checkFilmIdExist(updatedFilm.getId());
        return filmStorage.updateFilm(updatedFilm)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_ERR_MSG + updatedFilm.getId()));
    }

    private void checkUserIdExist(Long id) {
        if (!userStorage.hasUserId(id)) {
            log.trace(USER_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(USER_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkFilmIdExist(Long id) {
        if (!filmStorage.hasFilmId(id)) {
            log.trace(FILM_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkFilmIdNotExist(Long id) {
        if (filmStorage.hasFilmId(id)) {
            log.trace(DUPLICATE_FILM_FOUND_TRACE_MSG, id);
            throw new DuplicatedDataException(DUPLICATE_FILM_ERR_MSG + id);
        }
    }
}
