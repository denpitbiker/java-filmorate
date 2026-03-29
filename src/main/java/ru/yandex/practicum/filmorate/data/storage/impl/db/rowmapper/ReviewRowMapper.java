package ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.data.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRowMapper implements RowMapper<Review> {
    private static final String ID_COLUMN_LABEL = "id";
    private static final String CONTENT_COLUMN_LABEL = "content";
    private static final String IS_POSITIVE_COLUMN_LABEL = "is_positive";
    private static final String USER_ID_COLUMN_LABEL = "user_id";
    private static final String FILM_ID_COLUMN_LABEL = "film_id";
    private static final String RATE_COLUMN_LABEL = "rate";

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Review(
                rs.getLong(ID_COLUMN_LABEL),
                rs.getString(CONTENT_COLUMN_LABEL),
                rs.getBoolean(IS_POSITIVE_COLUMN_LABEL),
                rs.getLong(USER_ID_COLUMN_LABEL),
                rs.getLong(FILM_ID_COLUMN_LABEL),
                rs.getInt(RATE_COLUMN_LABEL)
        );
    }
}
