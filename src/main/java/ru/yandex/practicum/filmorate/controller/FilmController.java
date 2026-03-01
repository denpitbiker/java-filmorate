package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(FilmController.CONTROLLER_ROUTE)
@RestController
public class FilmController {
    private static final String ID_PATH_VAR = "id";
    private static final String USER_ID_PATH_VAR = "userId";
    private static final String COUNT_PARAM = "count";

    public static final String CONTROLLER_ROUTE = "/films";
    public static final String GET_FILM_SUBROUTE = "/{id}";
    public static final String LIKE_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}/like/{" + USER_ID_PATH_VAR + "}";
    public static final String UNLIKE_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}/like/{" + USER_ID_PATH_VAR + "}";
    public static final String GET_TOP_FILMS_SUBROUTE = "/popular";

    private static final String DEFAULT_TOP_FILMS_RETURN_COUNT = "10";

    private static final String GET_FILM_LOG_MSG = "Get film {} request";
    private static final String LIKE_FILM_LOG_MSG = "Like film {} by user {} request";
    private static final String UNLIKE_FILM_LOG_MSG = "Unlike film {} by user {} request";
    private static final String GET_TOP_FILMS_LOG_MSG = "Get top {} films request";
    private static final String GET_FILMS_LOG_MSG = "Get all films request";
    private static final String ADD_FILM_LOG_MSG = "Add new film request {}";
    private static final String UPDATE_FILM_LOG_MSG = "Update film request {}";

    private final FilmService filmService;

    @PutMapping(LIKE_FILM_SUBROUTE)
    public void likeFilm(@PathVariable(ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(LIKE_FILM_LOG_MSG, id, userId);
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping(UNLIKE_FILM_SUBROUTE)
    public void unlikeFilm(@PathVariable(ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(UNLIKE_FILM_LOG_MSG, id, userId);
        filmService.unlikeFilm(id, userId);
    }

    @GetMapping(GET_TOP_FILMS_SUBROUTE)
    public Collection<Film> getFilmsTop(
            @RequestParam(value = COUNT_PARAM, defaultValue = DEFAULT_TOP_FILMS_RETURN_COUNT) Integer count
    ) {
        log.info(GET_TOP_FILMS_LOG_MSG, count);
        return filmService.getFilmsTop(count);
    }

    @GetMapping(GET_FILM_SUBROUTE)
    public Film getFilm(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(GET_FILM_LOG_MSG, id);
        return filmService.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info(GET_FILMS_LOG_MSG);
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film newFilm) {
        log.info(ADD_FILM_LOG_MSG, newFilm);
        return filmService.addFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info(UPDATE_FILM_LOG_MSG, updatedFilm);
        return filmService.updateFilm(updatedFilm);
    }
}
