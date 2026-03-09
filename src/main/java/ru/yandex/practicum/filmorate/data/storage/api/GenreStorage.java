package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> getAllGenres();

    Optional<Genre> getGenre(Long id);
}