package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.data.storage.api.GenreStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genre";
    private static final String GET_GENRE_QUERY = "SELECT * FROM genre WHERE id = ?";

    private static final String GET_GENRES_LOG = "Searching for genres";
    private static final String GET_GENRE_LOG = "Searching for genre with id = {}";
    private static final String GET_GENRE_FAILED_LOG = "Failed to find genre with id = {}";

    private final JdbcTemplate jdbc;
    private static final GenreRowMapper mapper = new GenreRowMapper();

    @Override
    public Collection<Genre> getAllGenres() {
        log.trace(GET_GENRES_LOG);
        return jdbc.query(GET_ALL_GENRES_QUERY, mapper);
    }

    @Override
    public Optional<Genre> getGenre(Long id) {
        log.trace(GET_GENRE_LOG, id);
        if (id == null) return Optional.empty();
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_GENRE_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            log.info(GET_GENRE_FAILED_LOG, id);
            return Optional.empty();
        }
    }
}
