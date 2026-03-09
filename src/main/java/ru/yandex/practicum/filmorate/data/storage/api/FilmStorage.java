package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    boolean hasFilmId(Long id);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Film addFilm(Film newFilm);

    Optional<Film> getFilm(Long id);

    Film updateFilm(Film updatedFilm);

    Film removeFilm(Long id);

    List<Film> getAllFilms();
}
