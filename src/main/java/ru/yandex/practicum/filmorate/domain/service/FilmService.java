package ru.yandex.practicum.filmorate.domain.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.storage.api.GenreStorage;
import ru.yandex.practicum.filmorate.data.storage.api.MpaStorage;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;
import ru.yandex.practicum.filmorate.domain.mapper.FilmToFilmDtoMapper;
import ru.yandex.practicum.filmorate.domain.model.FilmAdditionalInfo;
import ru.yandex.practicum.filmorate.domain.model.FilmsAdditionalInfo;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final String GET_FILM_LOG_MSG = "Get film {}";
    private static final String LIKE_FILM_LOG_MSG = "Is success like film {} by user {}: {}";
    private static final String UNLIKE_FILM_LOG_MSG = "Is success unlike film {} by user {}: {}";
    private static final String GET_TOP_FILMS_LOG_MSG = "Get top {} films";
    private static final String GET_POPULAR_FILMS_LOG_MSG = "Get popular {} films in genre {} {} year";
    private static final String GET_FILMS_LOG_MSG = "Get all films";
    private static final String ADD_FILM_LOG_MSG = "Add new film {}";
    private static final String UPDATE_FILM_LOG_MSG = "Update film {}";
    private static final String USER_NOT_FOUND_TRACE_MSG = "Can't find user with id: {}";
    private static final String MPA_NOT_FOUND_TRACE_MSG = "Can't find mpa with id: {}";
    private static final String GENRE_NOT_FOUND_TRACE_MSG = "Can't find genre with id: {}";
    private static final String DUPLICATE_FILM_FOUND_TRACE_MSG = "Already have film with id: {}";
    private static final String FILM_NOT_FOUND_TRACE_MSG = "Can't find film with id: {}";

    private static final String GENRE_NOT_FOUND_ERR_MSG = "Can't find genre with id = ";
    private static final String MPA_NOT_FOUND_ERR_MSG = "Can't find mpa with id = ";
    private static final String USER_NOT_FOUND_ERR_MSG = "Can't find user with id = ";
    private static final String DUPLICATE_FILM_ERR_MSG = "Film already exists with id = ";
    private static final String FILM_NOT_FOUND_ERR_MSG = "Can't find film with id = ";
    private static final String FILMS_COUNT_ERR_MSG = "Films count must be positive number!";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private static final FilmToFilmDtoMapper filmMapper = new FilmToFilmDtoMapper();

    public FilmService(@DbStorage FilmStorage filmStorage, @DbStorage UserStorage userStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public void likeFilm(Long id, Long userId) {
        getFilmOrThrow(id);
        checkUserIdExist(userId);
        boolean isSuccess = filmStorage.addLike(id, userId);
        log.info(LIKE_FILM_LOG_MSG, id, userId, isSuccess);
    }

    public void unlikeFilm(Long id, Long userId) {
        getFilmOrThrow(id);
        checkUserIdExist(userId);
        boolean isSuccess = filmStorage.removeLike(id, userId);
        log.info(UNLIKE_FILM_LOG_MSG, id, userId, isSuccess);
    }

    public Collection<FilmDto> getFilmsPopulars(Integer count) {
        log.info(GET_TOP_FILMS_LOG_MSG, count);
        return getFilmsPopulars(count, null, null);
    }

    public Collection<FilmDto> getFilmsPopulars(Integer count, Long genreId, Integer year) {
        log.info(GET_POPULAR_FILMS_LOG_MSG, count, genreId, year);

        if (count == null || count <= 0) {
            throw new ValidationException(FILMS_COUNT_ERR_MSG);
        }

        List<Film> films = filmStorage.getPopularFilms(count, genreId, year);
        FilmsAdditionalInfo info = getFilmsInfo(films);

        return films.stream()
                .map(film -> filmMapper.toPresentation(film, extractFilmInfo(info, film)))
                .toList();
    }

    public FilmDto getFilm(Long id) {
        log.info(GET_FILM_LOG_MSG, id);
        return getFilmDtoOrThrow(id);
    }

    public Collection<FilmDto> getAllFilms() {
        log.info(GET_FILMS_LOG_MSG);
        List<Film> films = filmStorage.getAllFilms();
        FilmsAdditionalInfo info = getFilmsInfo(films);
        return films.stream()
                .map(film -> filmMapper.toPresentation(film, extractFilmInfo(info, film)))
                .toList();
    }

    public FilmDto addFilm(FilmDto newFilm) {
        log.info(ADD_FILM_LOG_MSG, newFilm);
        checkFilmIdNotExist(newFilm.getId());
        checkGenresExists(newFilm.getGenres());
        checkMpaExists(newFilm.getMpa().getId());
        Film film = filmStorage.addFilm(filmMapper.toData(newFilm));
        tryUpdateGenres(film.getId(), newFilm);
        return filmMapper.toPresentation(film, getFilmInfo(film));
    }

    public FilmDto updateFilm(FilmDto updatedFilm) {
        log.info(UPDATE_FILM_LOG_MSG, updatedFilm);
        checkFilmIdExist(updatedFilm.getId());
        checkGenresExists(updatedFilm.getGenres());
        checkMpaExists(updatedFilm.getMpa().getId());
        Film film = filmStorage.updateFilm(filmMapper.toData(updatedFilm));
        tryUpdateGenres(film.getId(), updatedFilm);
        return filmMapper.toPresentation(film, getFilmInfo(film));
    }

    private void tryUpdateGenres(Long filmId, FilmDto film) {
        if (film.getGenres() != null)
            genreStorage.updateFilmGenres(filmId, film.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet()));
    }

    private Film getFilmOrThrow(Long id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id));
    }

    private FilmDto getFilmDtoOrThrow(Long id) {
        Film film = getFilmOrThrow(id);
        return filmMapper.toPresentation(film, getFilmInfo(film));
    }

    private FilmAdditionalInfo extractFilmInfo(FilmsAdditionalInfo info, Film film) {
        return new FilmAdditionalInfo(
                info.mpas().get(film.getMpaId()),
                info.genres().get(film.getId()),
                info.likes().get(film.getId())
        );
    }

    private FilmAdditionalInfo getFilmInfo(Film film) {
        return extractFilmInfo(getFilmsInfo(Collections.singletonList(film)), film);
    }

    private FilmsAdditionalInfo getFilmsInfo(List<Film> films) {
        if (films.isEmpty()) {
            return new FilmsAdditionalInfo(
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    Collections.emptyMap()
            );
        }

        return new FilmsAdditionalInfo(
                mpaStorage.getMpasInfo(films.stream().map(Film::getMpaId).collect(Collectors.toSet())),
                filmStorage.getFilmsLikes(films.stream().map(Film::getId).toList()),
                genreStorage.getGenresForFilms(films.stream().map(Film::getId).toList())
        );
    }

    private void checkGenresExists(Collection<GenreDto> genres) {
        if (genres == null) return;
        genres.forEach((genre) -> {
            if (genreStorage.getGenre(genre.getId()).isEmpty()) {
                log.trace(GENRE_NOT_FOUND_TRACE_MSG, genre.getId());
                throw new NotFoundException(GENRE_NOT_FOUND_ERR_MSG + genre.getId());
            }
        });
    }

    private void checkMpaExists(Long id) {
        if (id == null) return;
        if (mpaStorage.getMpa(id).isEmpty()) {
            log.trace(MPA_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(MPA_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkUserIdExist(Long id) {
        if (!userStorage.hasUserId(id)) {
            log.trace(USER_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(USER_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkFilmIdExist(Long id) {
        if (!filmStorage.hasFilmId(id)) {
            log.trace(FILM_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkFilmIdNotExist(Long id) {
        if (filmStorage.hasFilmId(id)) {
            log.trace(DUPLICATE_FILM_FOUND_TRACE_MSG, id);
            throw new DuplicatedDataException(DUPLICATE_FILM_ERR_MSG + id);
        }
    }
}
