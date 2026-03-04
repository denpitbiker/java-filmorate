package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    boolean hasFilmId(Long id);

    Film addFilm(Film newFilm);

    Optional<Film> getFilm(Long id);

    Film updateFilm(Film updatedFilm);

    Film removeFilm(Long id);

    List<Film> getAllFilms();
}
