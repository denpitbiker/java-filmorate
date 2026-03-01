package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    private static final String FILMS_COUNT_ERR_MSG = "Films count must be positive number!";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesComparator filmLikesComparator = new FilmLikesComparator(true);

    public void likeFilm(Long id, Long userId) {
        Film film = filmStorage.getFilm(id);
        userStorage.getUser(userId);
        boolean isSuccess = film.getLikes().add(userId);
        log.info(LIKE_FILM_LOG_MSG, id, userId, isSuccess);
        filmStorage.updateFilm(film);
    }

    public void unlikeFilm(Long id, Long userId) {
        Film film = filmStorage.getFilm(id);
        userStorage.getUser(userId);
        boolean isSuccess = film.getLikes().remove(userId);
        log.info(UNLIKE_FILM_LOG_MSG, id, userId, isSuccess);
        filmStorage.updateFilm(film);
    }

    public Collection<Film> getFilmsTop(Integer count) {
        log.info(GET_TOP_FILMS_LOG_MSG, count);
        if (count <= 0) throw new ValidationException(FILMS_COUNT_ERR_MSG);
        return filmStorage.getAllFilms().stream().sorted(filmLikesComparator).limit(count).toList();
    }

    public Film getFilm(Long id) {
        log.info(GET_FILM_LOG_MSG, id);
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getAllFilms() {
        log.info(GET_FILMS_LOG_MSG);
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film newFilm) {
        log.info(ADD_FILM_LOG_MSG, newFilm);
        return filmStorage.addFilm(newFilm);
    }

    public Film updateFilm(Film updatedFilm) {
        log.info(UPDATE_FILM_LOG_MSG, updatedFilm);
        return filmStorage.updateFilm(updatedFilm);
    }
}
