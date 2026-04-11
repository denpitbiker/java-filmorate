package ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.data.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserRowMapper implements RowMapper<User> {
    private static final String ID_COLUMN_LABEL = "id";
    private static final String NAME_COLUMN_LABEL = "name";
    private static final String EMAIL_COLUMN_LABEL = "email";
    private static final String LOGIN_COLUMN_LABEL = "login";
    private static final String BIRTHDAY_COLUMN_LABEL = "birthday";

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong(ID_COLUMN_LABEL),
                rs.getString(EMAIL_COLUMN_LABEL),
                rs.getString(LOGIN_COLUMN_LABEL),
                rs.getString(NAME_COLUMN_LABEL),
                rs.getObject(BIRTHDAY_COLUMN_LABEL, LocalDate.class)
        );
    }
}
