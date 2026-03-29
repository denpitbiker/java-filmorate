package ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.data.model.Event;
import ru.yandex.practicum.filmorate.data.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.data.model.enums.EventType;

public class EventRowMapper implements RowMapper<Event> {
    public static final String ID_COLUMN_LABEL = "event_id";
    public static final String USER_ID_COLUMN_LABEL = "user_id";
    public static final String EVENT_TYPE_ID_COLUMN_LABEL = "event_type";
    public static final String EVENT_OPERATION_ID_COLUMN_LABEL = "operation";
    public static final String ENTITY_ID_COLUMN_LABEL = "entity_id";
    public static final String TIMESTAMP_COLUMN_LABEL = "timestamp";

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event(
                rs.getLong(ID_COLUMN_LABEL),
                rs.getLong(USER_ID_COLUMN_LABEL),
                rs.getLong(ENTITY_ID_COLUMN_LABEL),
                EventType.fromOrdinal(rs.getInt(EVENT_TYPE_ID_COLUMN_LABEL)),
                EventOperation.fromOrdinal(rs.getInt(EVENT_OPERATION_ID_COLUMN_LABEL)),
                rs.getTimestamp(TIMESTAMP_COLUMN_LABEL).toLocalDateTime()
        );
    }
}
