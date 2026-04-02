package ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.data.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorRowMapper implements RowMapper<Director> {
    private static final String ID_COLUMN_LABEL = "id";
    private static final String NAME_COLUMN_LABEL = "name";

    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Director(
                rs.getLong(ID_COLUMN_LABEL),
                rs.getString(NAME_COLUMN_LABEL)
        );
    }
}
