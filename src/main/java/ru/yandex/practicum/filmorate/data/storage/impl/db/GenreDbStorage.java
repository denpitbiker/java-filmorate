package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.common.model.Pair;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.data.storage.api.GenreStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.GenreRowMapper;

import java.util.*;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genre ORDER BY id";
    private static final String GET_GENRE_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String GET_GENRES_FOR_FILMS_QUERY = """
            SELECT fg.film_id AS film_id, fg.genre_id AS genre_id, g.name AS genre_name
            FROM film_genre AS fg
            JOIN genre AS g ON g.id = fg.genre_id AND film_id IN (%s)
            """;
    private static final String ADD_GENRES_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String REMOVE_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ?";

    private static final String GET_GENRES_LOG = "Searching for genres";
    private static final String GET_GENRE_LOG = "Searching for genre with id = {}";
    private static final String GET_GENRE_FAILED_LOG = "Failed to find genre with id = {}";

    private static final String FILM_ID_COLUMN_LABEL = "film_id";
    private static final String GENRE_ID_COLUMN_LABEL = "genre_id";
    private static final String GENRE_NAME_COLUMN_LABEL = "genre_name";

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

    @Override
    @Transactional
    public Map<Long, LinkedHashSet<Genre>> getGenresForFilms(List<Long> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        List<Pair<Long, Genre>> entries = jdbc.query(
                String.format(GET_GENRES_FOR_FILMS_QUERY, inSql),
                filmIds.toArray(),
                (rs, rowNum) -> new Pair<>(
                        rs.getLong(FILM_ID_COLUMN_LABEL),
                        new Genre(rs.getLong(GENRE_ID_COLUMN_LABEL), rs.getString(GENRE_NAME_COLUMN_LABEL))
                )
        );
        Map<Long, LinkedHashSet<Genre>> result = new HashMap<>();
        filmIds.forEach(filmId -> result.put(filmId, new LinkedHashSet<>()));
        entries.forEach(entry -> {
            result.get(entry.first).add(entry.second);
        });
        return result;
    }

    @Override
    public void updateFilmGenres(Long filmId, Set<Long> genresIds) {
        if (genresIds == null) return;
        jdbc.update(REMOVE_GENRES_QUERY, filmId);
        jdbc.batchUpdate(ADD_GENRES_QUERY, genresIds, genresIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }
}
