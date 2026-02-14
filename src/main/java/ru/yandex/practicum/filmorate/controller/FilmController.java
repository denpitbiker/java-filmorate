package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
@RestController
public class FilmController {
    private static final String GET_FILMS_LOG_MSG = "All films request";
    private static final String ADD_FILM_LOG_MSG = "Add new film request {}";
    private static final String UPDATE_FILM_LOG_MSG = "Update film request {}";

    private final FilmRepository filmRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Film> getAllFilms() {
        log.info(GET_FILMS_LOG_MSG);
        return filmRepository.getAllFilms();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film newFilm) {
        log.info(ADD_FILM_LOG_MSG, newFilm);
        return filmRepository.addFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info(UPDATE_FILM_LOG_MSG, updatedFilm);
        return filmRepository.updateFilm(updatedFilm);
    }
}
