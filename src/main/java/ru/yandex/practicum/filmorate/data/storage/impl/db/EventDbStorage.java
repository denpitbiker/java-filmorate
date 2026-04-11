package ru.yandex.practicum.filmorate.data.storage.impl.db;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Event;
import ru.yandex.practicum.filmorate.data.storage.api.EventStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.EventRowMapper;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private static final String GET_ALL_EVENTS_QUERY = "SELECT * FROM events ORDER BY timestamp DESC";
    private static final String GET_USER_EVENTS_QUERY = """
        SELECT e.*
        FROM events e
        WHERE e.user_id = ?
    """;
    private static final String ADD_EVENT_QUERY = "INSERT INTO events (user_id, entity_id, event_type, operation, timestamp)" +
            " VALUES (?, ?, ?, ?, ?) ";

    private static final String ID_COLUMN_LABEL = "event_id";

    private static final String GET_ALL_EVENTS_LOG = "Searching for events";
    private static final String GET_USER_EVENTS_LOG = "Searching for events for user with id = {}";

    private final JdbcTemplate jdbc;
    private static final EventRowMapper mapper = new EventRowMapper();

    @Override
    public Collection<Event> getAllEvents() {
        log.trace(GET_ALL_EVENTS_LOG);
        return jdbc.query(GET_ALL_EVENTS_QUERY, mapper);
    }

    @Override
    public Collection<Event> getEventsByUserId(Long userId) {
        log.trace(GET_USER_EVENTS_LOG, userId);
        return jdbc.query(GET_USER_EVENTS_QUERY, mapper, userId);
    }

    @Override
    public Event addEvent(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_EVENT_QUERY, new String[]{ID_COLUMN_LABEL});
            ps.setLong(1, event.getUserId());
            ps.setLong(2, event.getEntityId());
            ps.setLong(3, event.getEventType().ordinal());
            ps.setLong(4, event.getEventOperation().ordinal());
            ps.setTimestamp(5, Timestamp.valueOf(event.getTimestamp()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() == null)
            return null;

        Long eventId = keyHolder.getKey().longValue();
        event.setEventId(eventId);

        return event;
    }

}
