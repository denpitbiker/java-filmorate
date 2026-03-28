package ru.yandex.practicum.filmorate.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.domain.service.FilmService;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(FilmController.CONTROLLER_ROUTE)
@RestController
public class FilmController {
    private static final String ID_PATH_VAR = "id";
    private static final String USER_ID_PATH_VAR = "userId";
    public static final String COUNT_PARAM = "count";

    public static final String CONTROLLER_ROUTE = "/films";
    public static final String GET_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}";
    public static final String DELETE_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}";
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
    private static final String DELETE_FILM_LOG_MSG = "Delete film with id {} request";

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
    public Collection<FilmDto> getFilmsTop(
            @RequestParam(value = COUNT_PARAM, defaultValue = DEFAULT_TOP_FILMS_RETURN_COUNT) Integer count
    ) {
        log.info(GET_TOP_FILMS_LOG_MSG, count);
        return filmService.getFilmsTop(count);
    }

    @GetMapping(GET_FILM_SUBROUTE)
    public FilmDto getFilm(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(GET_FILM_LOG_MSG, id);
        return filmService.getFilm(id);
    }

    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        log.info(GET_FILMS_LOG_MSG);
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody FilmDto newFilm) {
        log.info(ADD_FILM_LOG_MSG, newFilm);
        return filmService.addFilm(newFilm);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto updatedFilm) {
        log.info(UPDATE_FILM_LOG_MSG, updatedFilm);
        return filmService.updateFilm(updatedFilm);
    }

    @DeleteMapping(DELETE_FILM_SUBROUTE)
    public FilmDto deleteFilm(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(DELETE_FILM_LOG_MSG, id);
        return filmService.deleteFilm(id);
    }
}
