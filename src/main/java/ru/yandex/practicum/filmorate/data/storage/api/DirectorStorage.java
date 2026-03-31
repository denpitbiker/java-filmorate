package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Director;

import java.util.*;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getDirector(Long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director deleteDirector(Long id);

    Map<Long, LinkedHashSet<Director>> getDirectorsForFilms(List<Long> filmIds);

    boolean hasDirectorId(Long id);

    public void updateFilmDirectors(Long filmId, Set<Long> directorsIds);
}
