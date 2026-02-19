package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

@Slf4j
@Repository
public class FilmRepository {
    private static final String ADDING_FILM_TRACE_MSG = "Adding new film: {}";
    private static final String DUPLICATE_FILM_FOUND_TRACE_MSG = "Already have film with id: {}";
    private static final String ADDED_FILM_TRACE_MSG = "New film added: {}";
    private static final String UPDATING_FILM_TRACE_MSG = "Updating film: {}";
    private static final String FILM_NOT_FOUND_TRACE_MSG = "Can't find film with id: {}";
    private static final String UPDATED_FILM_TRACE_MSG = "Film updated: {}";

    private static final String FILM_NOT_FOUND_ERR_MSG = "Can't find film with id = ";
    private static final String DUPLICATE_FILM_ERR_MSG = "Film already exists with id = ";

    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 0L;

    public Film addFilm(Film newFilm) {
        log.trace(ADDING_FILM_TRACE_MSG, newFilm);
        Long newFilmId = newFilm.getId();
        if (films.containsKey(newFilmId)) {
            log.trace(DUPLICATE_FILM_FOUND_TRACE_MSG, newFilmId);
            throw new DuplicatedDataException(DUPLICATE_FILM_ERR_MSG + newFilmId);
        }
        if (newFilmId == null) newFilmId = ++idCounter;
        else idCounter = max(idCounter, newFilmId);
        newFilm.setId(newFilmId);
        films.put(newFilmId, newFilm);
        log.trace(ADDED_FILM_TRACE_MSG, newFilm);
        return newFilm;
    }

    public Film updateFilm(Film updatedFilm) {
        log.trace(UPDATING_FILM_TRACE_MSG, updatedFilm);
        Long updatedFilmId = updatedFilm.getId();
        if (updatedFilmId == null || !films.containsKey(updatedFilmId)) {
            log.trace(FILM_NOT_FOUND_TRACE_MSG, updatedFilmId);
            throw new NotFoundException(FILM_NOT_FOUND_ERR_MSG + updatedFilmId);
        }
        films.put(updatedFilmId, updatedFilm);
        log.trace(UPDATED_FILM_TRACE_MSG, updatedFilm);
        return updatedFilm;
    }

    public List<Film> getAllFilms() {
        return films.values().stream().toList();
    }
}
