package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Mpa;
import ru.yandex.practicum.filmorate.data.storage.api.MpaStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private static final String GET_ALL_MPAS_QUERY = "SELECT * FROM mpa";
    private static final String GET_MPA_QUERY = "SELECT * FROM mpa WHERE id = ?";

    private static final String GET_MPAS_LOG = "Searching for mpas";
    private static final String GET_MPA_LOG = "Searching for mpa with id = {}";
    private static final String GET_MPA_FAILED_LOG = "Failed to find mpa with id = {}";

    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper = new MpaRowMapper();

    @Override
    public Collection<Mpa> getAllMpas() {
        log.trace(GET_MPAS_LOG);
        return jdbc.query(GET_ALL_MPAS_QUERY, mapper);
    }

    @Override
    public Optional<Mpa> getMpa(Long id) {
        log.trace(GET_MPA_LOG, id);
        if (id == null) return Optional.empty();
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_MPA_QUERY, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            log.info(GET_MPA_FAILED_LOG, id);
            return Optional.empty();
        }
    }
}
