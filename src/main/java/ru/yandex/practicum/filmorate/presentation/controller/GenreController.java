package ru.yandex.practicum.filmorate.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.domain.service.GenreService;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(GenreController.CONTROLLER_ROUTE)
@RestController
public class GenreController {
    private static final String ID_PATH_VAR = "id";

    public static final String CONTROLLER_ROUTE = "/genres";
    public static final String GET_MPA_SUBROUTE = "/{id}";

    private static final String GET_GENRE_LOG_MSG = "Get genre with id = {} request";
    private static final String GET_GENRES_LOG_MSG = "Get all genres request";

    private final GenreService genreService;

    @GetMapping(GET_MPA_SUBROUTE)
    public GenreDto getGenre(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(GET_GENRE_LOG_MSG, id);
        return genreService.getGenre(id);
    }

    @GetMapping
    public Collection<GenreDto> getAllGenres() {
        log.info(GET_GENRES_LOG_MSG);
        return genreService.getAllGenres();
    }
}
