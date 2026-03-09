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
import ru.yandex.practicum.filmorate.domain.mapper.FilmDtoToFilmMapper;
import ru.yandex.practicum.filmorate.domain.mapper.FilmToFilmDtoMapper;
import ru.yandex.practicum.filmorate.domain.tool.comparison.FilmLikesComparator;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private static final String GET_FILM_LOG_MSG = "Get film {}";
    private static final String LIKE_FILM_LOG_MSG = "Is success like film {} by user {}: {}";
    private static final String UNLIKE_FILM_LOG_MSG = "Is success unlike film {} by user {}: {}";
    private static final String GET_TOP_FILMS_LOG_MSG = "Get top {} films";
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
    private static final FilmLikesComparator filmLikesComparator = new FilmLikesComparator(true);
    private final FilmToFilmDtoMapper filmToFilmDtoMapper;
    private static final FilmDtoToFilmMapper filmDtoToFilmMapper = new FilmDtoToFilmMapper();

    public FilmService(@DbStorage FilmStorage filmStorage, @DbStorage UserStorage userStorage, GenreStorage genreStorage, MpaStorage mpaStorage, FilmToFilmDtoMapper filmToFilmDtoMapper) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.filmToFilmDtoMapper = filmToFilmDtoMapper;
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

    public Collection<FilmDto> getFilmsTop(Integer count) {
        log.info(GET_TOP_FILMS_LOG_MSG, count);
        if (count <= 0) throw new ValidationException(FILMS_COUNT_ERR_MSG);
        return filmStorage.getAllFilms().stream()
                .sorted(filmLikesComparator)
                .limit(count)
                .map(filmToFilmDtoMapper::map)
                .toList();
    }

    public FilmDto getFilm(Long id) {
        log.info(GET_FILM_LOG_MSG, id);
        return getFilmDtoOrThrow(id);
    }

    public Collection<FilmDto> getAllFilms() {
        log.info(GET_FILMS_LOG_MSG);
        return filmStorage.getAllFilms().stream()
                .map(filmToFilmDtoMapper::map)
                .toList();
    }

    public FilmDto addFilm(FilmDto newFilm) {
        log.info(ADD_FILM_LOG_MSG, newFilm);
        checkFilmIdNotExist(newFilm.getId());
        checkGenresExists(newFilm.getGenres());
        checkMpaExists(newFilm.getMpa().getId());
        return filmToFilmDtoMapper.map(filmStorage.addFilm(filmDtoToFilmMapper.map(newFilm)));
    }

    public FilmDto updateFilm(FilmDto updatedFilm) {
        log.info(UPDATE_FILM_LOG_MSG, updatedFilm);
        checkFilmIdExist(updatedFilm.getId());
        checkGenresExists(updatedFilm.getGenres());
        checkMpaExists(updatedFilm.getMpa().getId());
        return filmToFilmDtoMapper.map(filmStorage.updateFilm(filmDtoToFilmMapper.map(updatedFilm)));
    }

    private Film getFilmOrThrow(Long id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id));
    }

    private FilmDto getFilmDtoOrThrow(Long id) {
        return filmToFilmDtoMapper.map(getFilmOrThrow(id));
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
