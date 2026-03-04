package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

import static java.lang.Math.max;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private static final String ADDING_FILM_TRACE_MSG = "Adding new film: {}";
    private static final String GET_FILM_TRACE_MSG = "Get film: {}";
    private static final String GET_ALL_FILMS_TRACE_MSG = "Get all films";
    private static final String REMOVE_FILM_TRACE_MSG = "Remove film: {}";
    private static final String ADDED_FILM_TRACE_MSG = "New film added: {}";
    private static final String GOT_FILM_TRACE_MSG = "Got film: {}";
    private static final String REMOVED_FILM_TRACE_MSG = "Removed film: {}";
    private static final String UPDATING_FILM_TRACE_MSG = "Updating film: {}";
    private static final String UPDATED_FILM_TRACE_MSG = "Film updated: {}";

    private final Map<Long, Film> films = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Film addFilm(Film newFilm) {
        log.trace(ADDING_FILM_TRACE_MSG, newFilm);
        Long newFilmId = newFilm.getId();
        if (newFilmId == null) newFilmId = ++idCounter;
        else idCounter = max(idCounter, newFilmId);
        newFilm.setId(newFilmId);
        films.put(newFilmId, newFilm.clone());
        log.trace(ADDED_FILM_TRACE_MSG, newFilm);
        return newFilm;
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        log.trace(GET_FILM_TRACE_MSG, id);
        Film film = films.get(id);
        if (film == null) return Optional.empty();
        log.trace(GOT_FILM_TRACE_MSG, film);
        return Optional.of(film.clone());
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.trace(UPDATING_FILM_TRACE_MSG, updatedFilm);
        Long updatedFilmId = updatedFilm.getId();
        films.put(updatedFilmId, updatedFilm.clone());
        log.trace(UPDATED_FILM_TRACE_MSG, updatedFilm);
        return updatedFilm;
    }

    @Override
    public Film removeFilm(Long id) {
        log.trace(REMOVE_FILM_TRACE_MSG, id);
        Film removed = films.remove(id);
        log.trace(REMOVED_FILM_TRACE_MSG, removed);
        return removed;
    }

    @Override
    public List<Film> getAllFilms() {
        log.trace(GET_ALL_FILMS_TRACE_MSG);
        return films.values().stream()
                .map(Film::clone)
                .toList();
    }

    @Override
    public boolean hasFilmId(Long id) {
        return id != null && films.containsKey(id);
    }
}
