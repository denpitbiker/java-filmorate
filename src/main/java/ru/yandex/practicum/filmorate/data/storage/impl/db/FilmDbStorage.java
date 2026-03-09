package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String GET_FILM_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String ADD_FILM_QUERY = "INSERT INTO film (name, description, release_date, mpa_id, duration_minutes) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?, mpa_id = ?, duration_minutes = ? WHERE id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT * FROM film";
    private static final String DELETE_FILM_QUERY = "DELETE FROM film WHERE id=?";
    private static final String GET_GENRES_FOR_FILM_QUERY = "SELECT genre_id FROM film_genre WHERE film_id = ?";
    private static final String GET_LIKES_FOR_FILM_QUERY = "SELECT user_id FROM film_like WHERE film_id = ?";

    private static final String ID_COLUMN = "id";

    private static final String ADD_LIKE_QUERY = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";

    private static final String ADD_GENRES_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String REMOVE_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ?";

    private static final String GET_FILMS_LOG = "Searching for films";
    private static final String GET_FILM_FAILED_LOG = "Failed to find film with id = {}";

    private final JdbcTemplate jdbc;

    private static final FilmRowMapper mapper = new FilmRowMapper();

    @Override
    public boolean hasFilmId(Long id) {
        if (id == null) return false;
        return !jdbc.queryForList(GET_FILM_QUERY, id).isEmpty();
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        int rowsAffected = jdbc.update(ADD_LIKE_QUERY, filmId, userId);
        return rowsAffected > 0;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        int rowsAffected = jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
        return rowsAffected > 0;
    }

    @Override
    @Transactional
    public Film addFilm(Film newFilm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_FILM_QUERY, new String[]{ID_COLUMN});
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            ps.setLong(4, newFilm.getMpaId());
            ps.setLong(5, newFilm.getDurationMinutes());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() == null) return null;
        long filmId = keyHolder.getKey().longValue();
        newFilm.setId(filmId);
        updateGenresIfNecessary(newFilm);
        return newFilm;
    }

    @Override
    @Transactional
    public Optional<Film> getFilm(Long id) {
        try {
            Film film = jdbc.queryForObject(GET_FILM_QUERY, mapper, id);
            if (film == null) return Optional.empty();
            applyLikesAndGenres(film);
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            log.info(GET_FILM_FAILED_LOG, id);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Film updateFilm(Film updatedFilm) {
        Long filmId = updatedFilm.getId();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE_FILM_QUERY);
            ps.setString(1, updatedFilm.getName());
            ps.setString(2, updatedFilm.getDescription());
            ps.setDate(3, Date.valueOf(updatedFilm.getReleaseDate()));
            ps.setLong(4, updatedFilm.getMpaId());
            ps.setLong(5, updatedFilm.getDurationMinutes());
            ps.setLong(6, filmId);
            return ps;
        });
        updateGenresIfNecessary(updatedFilm);
        return updatedFilm;
    }

    @Override
    @Transactional
    public Film removeFilm(Long id) {
        Film removed = getFilm(id).orElse(null);
        if (removed != null) {
            jdbc.update(DELETE_FILM_QUERY, id);
        }
        return removed;
    }

    @Override
    @Transactional
    public List<Film> getAllFilms() {
        log.trace(GET_FILMS_LOG);
        return jdbc.query(GET_ALL_FILMS_QUERY, mapper).stream()
                .peek(this::applyLikesAndGenres)
                .toList();
    }

    private void updateGenresIfNecessary(Film film) {
        Long filmId = film.getId();
        Set<Long> genresIds = film.getGenresIds();
        if (genresIds == null) return;
        jdbc.update(REMOVE_GENRES_QUERY, filmId);
        jdbc.batchUpdate(ADD_GENRES_QUERY, genresIds, genresIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }

    private void applyLikesAndGenres(Film film) {
        film.getLikesIds().addAll(findLikes(film.getId()));
        film.getGenresIds().addAll(findGenres(film.getId()));
    }

    private List<Long> findLikes(Long id) {
        if (id == null) return new ArrayList<>();
        return jdbc.queryForList(GET_LIKES_FOR_FILM_QUERY, Long.class, id);
    }

    private List<Long> findGenres(Long id) {
        if (id == null) return new ArrayList<>();
        return jdbc.queryForList(GET_GENRES_FOR_FILM_QUERY, Long.class, id);
    }
}
