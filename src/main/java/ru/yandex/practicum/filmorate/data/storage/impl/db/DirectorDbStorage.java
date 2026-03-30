package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Director;
import ru.yandex.practicum.filmorate.data.storage.api.DirectorStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
@DbStorage
public class DirectorDbStorage implements DirectorStorage {
    private final static String ID_COLUMN_LABEL = "id";

    private final static String GET_ALL_DIRECTORS_QUERY = "SELECT * FROM director";
    private final static String GET_DIRECTOR_QUERY = "SELECT * FROM director WHERE id = ?";
    private final static String ADD_DIRECTOR_QUERY = "INSERT INTO director (id, name) VALUES (?, ?)";
    private final static String UPDATE_DIRECTOR_QUERY = "UPDATE director SET name = ? WHERE id = ?";
    private final static String DELETE_DIRECTOR_QUERY = "DELETE FROM director WHERE id = ?";

    private final JdbcTemplate jdbc;

    private static final DirectorRowMapper mapper = new DirectorRowMapper();

    @Override
    @Transactional
    public List<Director> getAllDirectors() {
        return jdbc.query(GET_ALL_DIRECTORS_QUERY, mapper);
    }

    @Override
    public Optional<Director> getDirector(Long id) {
        return Optional.ofNullable(jdbc.queryForObject(GET_DIRECTOR_QUERY, mapper, id));
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

    public boolean hasDirectorId(Long id) {
        if (id == null) return false;
        return !jdbc.queryForList(GET_DIRECTOR_QUERY, id).isEmpty();
    }
}
