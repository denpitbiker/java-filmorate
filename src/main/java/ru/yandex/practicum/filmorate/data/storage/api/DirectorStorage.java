package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getDirector(Long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director deleteDirector(Long id);

    boolean hasDirectorId(Long id);
}
