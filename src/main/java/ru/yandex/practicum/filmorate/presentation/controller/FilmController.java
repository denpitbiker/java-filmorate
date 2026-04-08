package ru.yandex.practicum.filmorate.presentation.controller;

import java.util.Collection;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.data.model.enums.SortBy;
import ru.yandex.practicum.filmorate.domain.service.EventService;
import ru.yandex.practicum.filmorate.domain.service.FilmService;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(FilmController.CONTROLLER_ROUTE)
@RestController
public class FilmController {
    private static final String ID_PATH_VAR = "id";
    private static final String USER_ID_PATH_VAR = "userId";
    public static final String DIRECTOR_ID_VAR = "directorId";
    public static final String COUNT_PARAM = "count";
    public static final String GENRE_PARAM = "genreId";
    public static final String YEAR_PARAM = "year";
    public static final String SORT_BY_PARAM = "sortBy";
    public static final String BY_PARAM = "by";
    public static final String QUERY_VAR = "query";
    private static final String FRIEND_ID_PARAM = "friendId";

    public static final String CONTROLLER_ROUTE = "/films";
    public static final String GET_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}";
    public static final String DELETE_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}";
    public static final String LIKE_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}/like/{" + USER_ID_PATH_VAR + "}";
    public static final String UNLIKE_FILM_SUBROUTE = "/{" + ID_PATH_VAR + "}/like/{" + USER_ID_PATH_VAR + "}";
    public static final String GET_TOP_FILMS_SUBROUTE = "/popular";
    public static final String FILMS_SEARCH_SUBROUTE = "/search";
    public static final String GET_DIRECTOR_FILMS_SUBROUTE = "/director/{" + DIRECTOR_ID_VAR + "}";
    public static final String GET_COMMON_FILMS_SUBROUTE = "/common";

    private static final String DEFAULT_TOP_FILMS_RETURN_COUNT = "10";

    private static final String GET_FILM_LOG_MSG = "Get film {} request";
    private static final String LIKE_FILM_LOG_MSG = "Like film {} by user {} request";
    private static final String UNLIKE_FILM_LOG_MSG = "Unlike film {} by user {} request";
    private static final String GET_TOP_FILMS_LOG_MSG = "Get top {} films request";
    private static final String GET_FILMS_LOG_MSG = "Get all films request";
    private static final String ADD_FILM_LOG_MSG = "Add new film request {}";
    private static final String UPDATE_FILM_LOG_MSG = "Update film request {}";
    private static final String DELETE_FILM_LOG_MSG = "Delete film with id {} request";
    private static final String GET_DIRECTOR_FILMS_MSG = "Get films of director with id {} request";
    private static final String SEARCH_FILMS_MSG = "Search films with query {} and by {} request";
    private static final String GET_COMMON_FILMS_LOG_MSG = "Get common films for users {} and {} request";
    private final FilmService filmService;
    private final EventService eventService;

    @PutMapping(LIKE_FILM_SUBROUTE)
    public void likeFilm(@PathVariable(ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(LIKE_FILM_LOG_MSG, id, userId);
        filmService.likeFilm(id, userId);
        eventService.createLikeFilmEvent(userId, id);
    }

    @DeleteMapping(UNLIKE_FILM_SUBROUTE)
    public void unlikeFilm(@PathVariable(ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(UNLIKE_FILM_LOG_MSG, id, userId);
        filmService.unlikeFilm(id, userId);
        eventService.createUnlikeFilmEvent(userId, id);
    }

    @GetMapping(GET_TOP_FILMS_SUBROUTE)
    public Collection<FilmDto> getPopularFilms(
            @RequestParam(value = COUNT_PARAM, defaultValue = DEFAULT_TOP_FILMS_RETURN_COUNT) Integer count,
            @RequestParam(value = GENRE_PARAM, required = false) Long genreId,
            @RequestParam(value = YEAR_PARAM, required = false) Integer year) {
        log.info(GET_TOP_FILMS_LOG_MSG, count);
        return filmService.getFilmsPopulars(count, genreId, year);
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

    @GetMapping(GET_DIRECTOR_FILMS_SUBROUTE)
    public Collection<FilmDto> getDirectorFilms(
            @PathVariable(DIRECTOR_ID_VAR) Long id,
            @RequestParam(value = SORT_BY_PARAM, defaultValue = "year") SortBy sortBy) {
        log.info(GET_DIRECTOR_FILMS_MSG, id);
        return filmService.getDirectorFilms(id, sortBy);
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

    @GetMapping(FILMS_SEARCH_SUBROUTE)
    public Collection<FilmDto> searchFilms(
            @RequestParam(QUERY_VAR) String query,
            @RequestParam(BY_PARAM) String by) {
        log.info(SEARCH_FILMS_MSG, query, by);
        return filmService.searchFilms(query, by);
    }

    @GetMapping(GET_COMMON_FILMS_SUBROUTE)
    public Collection<FilmDto> getCommonFilms(
            @RequestParam(USER_ID_PATH_VAR) Long userId,
            @RequestParam(FRIEND_ID_PARAM) Long friendId) {
        log.info(GET_COMMON_FILMS_LOG_MSG, userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}
