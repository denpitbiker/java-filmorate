package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.common.model.Pair;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Director;
import ru.yandex.practicum.filmorate.data.storage.api.DirectorStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Repository
@DbStorage
public class DirectorDbStorage implements DirectorStorage {
    private final static String ID_COLUMN_LABEL = "id";

    private static final String GET_ALL_DIRECTORS_QUERY = "SELECT * FROM director";
    private static final String GET_DIRECTOR_QUERY = "SELECT * FROM director WHERE id = ?";
    private static final String ADD_DIRECTOR_QUERY = "INSERT INTO director (name) VALUES (?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE director SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM director WHERE id = ?";
    private static final String GET_DIRECTORS_FOR_FILMS_QUERY = """
            SELECT fd.film_id AS film_id, fd.director_id AS director_id, d.name AS director_name
            FROM film_director AS fd
            JOIN director AS d ON d.id = fd.director_id AND film_id IN (%s)
            """;
    private static final String DELETE_DIRECTORS_OF_FILM_QUERY = "DELETE FROM film_director WHERE film_id = ?";
    private static final String ADD_DIRECTORS_OF_FILM_QUERY = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";

    private static final String FILM_ID_COLUMN_LABEL = "film_id";
    private static final String DIRECTOR_ID_COLUMN_LABEL = "director_id";
    private static final String DIRECTOR_NAME_COLUMN_LABEL = "director_name";

    private final JdbcTemplate jdbc;

    private static final DirectorRowMapper mapper = new DirectorRowMapper();

    @Override
    @Transactional
    public List<Director> getAllDirectors() {
        return jdbc.query(GET_ALL_DIRECTORS_QUERY, mapper);
    }

    @Override
    public Optional<Director> getDirector(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_DIRECTOR_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Director addDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(ADD_DIRECTOR_QUERY, new String[]{ ID_COLUMN_LABEL });
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() == null) {
            return null;
        }
        long id = keyHolder.getKey().longValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        Long id = director.getId();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(UPDATE_DIRECTOR_QUERY);
            ps.setString(1, director.getName());
            ps.setLong(2, id);
            return ps;
        });
        return director;
    }

    @Override
    public Director deleteDirector(Long id) {
        Director director = getDirector(id).orElse(null);
        if (director != null) {
            jdbc.update(DELETE_DIRECTOR_QUERY, id);
        }
        return director;
    }

    @Override
    @Transactional
    public Map<Long, LinkedHashSet<Director>> getDirectorsForFilms(List<Long> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        List<Pair<Long, Director>> entries = jdbc.query(
                String.format(GET_DIRECTORS_FOR_FILMS_QUERY, inSql),
                filmIds.toArray(),
                (rs, rowNum) -> new Pair<>(
                        rs.getLong(FILM_ID_COLUMN_LABEL),
                        new Director(rs.getLong(DIRECTOR_ID_COLUMN_LABEL), rs.getString(DIRECTOR_NAME_COLUMN_LABEL))
                )
        );
        Map<Long, LinkedHashSet<Director>> result = new HashMap<>();
        entries.forEach(entry -> {
            if (!result.containsKey(entry.first)) {
                result.put(entry.first, new LinkedHashSet<>());
            }
            result.get(entry.first).add(entry.second);
        });
        return result;
    }

    @Override
    public boolean hasDirectorId(Long id) {
        if (id == null) return false;
        return !jdbc.queryForList(GET_DIRECTOR_QUERY, id).isEmpty();
    }

    @Override
    public void updateFilmDirectors(Long filmId, Set<Long> directorsIds) {
        if (directorsIds == null) return;
        jdbc.update(DELETE_DIRECTORS_OF_FILM_QUERY, filmId);
        jdbc.batchUpdate(ADD_DIRECTORS_OF_FILM_QUERY, directorsIds, directorsIds.size(),
                (ps, directorId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, directorId);
                });
    }
}
