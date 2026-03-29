package ru.yandex.practicum.filmorate.data.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.data.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.data.model.enums.EventType;

@Data
@Builder
@AllArgsConstructor
public class Event implements Cloneable {

    Long eventId;
    Long userId;
    Long entityId;
    EventType eventType;
    EventOperation eventOperation;
    LocalDateTime timestamp;

    @Override
    public Event clone() {
        return new Event(eventId, userId, entityId, eventType, eventOperation, timestamp);
    }
}
