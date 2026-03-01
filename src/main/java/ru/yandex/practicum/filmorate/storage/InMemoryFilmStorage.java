package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private static final String ADDING_FILM_TRACE_MSG = "Adding new film: {}";
    private static final String GET_FILM_TRACE_MSG = "Get film: {}";
    private static final String GET_ALL_FILMS_TRACE_MSG = "Get all films";
    private static final String REMOVE_FILM_TRACE_MSG = "Remove film: {}";
    private static final String DUPLICATE_FILM_FOUND_TRACE_MSG = "Already have film with id: {}";
    private static final String ADDED_FILM_TRACE_MSG = "New film added: {}";
    private static final String GOT_FILM_TRACE_MSG = "Got film: {}";
    private static final String REMOVED_FILM_TRACE_MSG = "Removed film: {}";
    private static final String UPDATING_FILM_TRACE_MSG = "Updating film: {}";
    private static final String FILM_NOT_FOUND_TRACE_MSG = "Can't find film with id: {}";
    private static final String UPDATED_FILM_TRACE_MSG = "Film updated: {}";

    private static final String FILM_NOT_FOUND_ERR_MSG = "Can't find film with id = ";
    private static final String DUPLICATE_FILM_ERR_MSG = "Film already exists with id = ";

    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Film addFilm(Film newFilm) {
        log.trace(ADDING_FILM_TRACE_MSG, newFilm);
        Long newFilmId = newFilm.getId();
        if (films.containsKey(newFilmId)) {
            log.trace(DUPLICATE_FILM_FOUND_TRACE_MSG, newFilmId);
            throw new DuplicatedDataException(DUPLICATE_FILM_ERR_MSG + newFilmId);
        }
        if (newFilmId == null) newFilmId = ++idCounter;
        else idCounter = max(idCounter, newFilmId);
        initLikesStorageIfNull(newFilm);
        newFilm.setId(newFilmId);
        films.put(newFilmId, newFilm.clone());
        log.trace(ADDED_FILM_TRACE_MSG, newFilm);
        return newFilm;
    }

    @Override
    public Film getFilm(Long id) {
        log.trace(GET_FILM_TRACE_MSG, id);
        checkFilmIdExists(id);
        Film film = films.get(id).clone();
        log.trace(GOT_FILM_TRACE_MSG, film);
        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.trace(UPDATING_FILM_TRACE_MSG, updatedFilm);
        Long updatedFilmId = updatedFilm.getId();
        checkFilmIdExists(updatedFilmId);
        initLikesStorageIfNull(updatedFilm);
        films.put(updatedFilmId, updatedFilm.clone());
        log.trace(UPDATED_FILM_TRACE_MSG, updatedFilm);
        return updatedFilm;
    }

    @Override
    public Film removeFilm(Long id) {
        log.trace(REMOVE_FILM_TRACE_MSG, id);
        checkFilmIdExists(id);
        Film removed = films.remove(id);
        log.trace(REMOVED_FILM_TRACE_MSG, removed);
        return removed;
    }

    @Override
    public List<Film> getAllFilms() {
        log.trace(GET_ALL_FILMS_TRACE_MSG);
        return films.values().stream().map(Film::clone).toList();
    }

    private void checkFilmIdExists(Long id) {
        if (id == null || !films.containsKey(id)) {
            log.trace(FILM_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void initLikesStorageIfNull(Film film) {
        if (film.getLikes() == null) film.setLikes(new HashSet<>());
    }
}
