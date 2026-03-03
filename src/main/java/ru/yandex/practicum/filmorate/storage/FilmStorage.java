package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    boolean hasFilmId(Long id);

    Optional<Film> addFilm(Film newFilm);

    Optional<Film> getFilm(Long id);

    Optional<Film> updateFilm(Film updatedFilm);

    Optional<Film> removeFilm(Long id);

    List<Film> getAllFilms();
}
