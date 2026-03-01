package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film newFilm);

    Film getFilm(Long id);

    Film updateFilm(Film updatedFilm);

    Film removeFilm(Long id);

    List<Film> getAllFilms();
}
