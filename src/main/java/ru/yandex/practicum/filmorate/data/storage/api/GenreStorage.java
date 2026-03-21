package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Genre;

import java.util.*;

public interface GenreStorage {

    Collection<Genre> getAllGenres();

    Optional<Genre> getGenre(Long id);

    Map<Long, LinkedHashSet<Genre>> getGenresForFilms(List<Long> filmIds);

    void updateFilmGenres(Long filmId, Set<Long> genresIds);
}