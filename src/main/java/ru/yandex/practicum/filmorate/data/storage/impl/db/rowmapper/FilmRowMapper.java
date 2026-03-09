package ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.data.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;

public class FilmRowMapper implements RowMapper<Film> {
    private static final String ID_COLUMN_LABEL = "id";
    private static final String NAME_COLUMN_LABEL = "name";
    private static final String DESCRIPTION_COLUMN_LABEL = "description";
    private static final String RELEASE_DATE_COLUMN_LABEL = "release_date";
    private static final String MPA_ID_COLUMN_LABEL = "mpa_id";
    private static final String DURATION_MINUTES_COLUMN_LABEL = "duration_minutes";

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong(ID_COLUMN_LABEL),
                rs.getString(NAME_COLUMN_LABEL),
                rs.getString(DESCRIPTION_COLUMN_LABEL),
                rs.getObject(RELEASE_DATE_COLUMN_LABEL, LocalDate.class),
                rs.getLong(MPA_ID_COLUMN_LABEL),
                rs.getLong(DURATION_MINUTES_COLUMN_LABEL),
                new HashSet<>(),
                new HashSet<>()
        );
    }
}
