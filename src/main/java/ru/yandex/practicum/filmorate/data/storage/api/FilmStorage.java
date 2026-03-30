package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    boolean hasFilmId(Long id);

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Map<Long, Set<Long>> getFilmsLikes(List<Long> filmIds);

    Film addFilm(Film newFilm);

    Optional<Film> getFilm(Long id);

    Film updateFilm(Film updatedFilm);

    Film removeFilm(Long id);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(Integer limit, Long genreId, Integer year);

    List<Film> getDirectorFilms(Long id, String sortBy);
}
