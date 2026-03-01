package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film newFilm);
    Film getFilm(Long id);
    Film updateFilm(Film updatedFilm);
    Film deleteFilm(Long id);
    List<Film> getAllFilms();
}
