package ru.yandex.practicum.filmorate.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto implements Cloneable {

    Long eventId;
    Long userId;
    Long entityId;
    String eventType;
    String operation;
    Long timestamp;

    @Override
    protected EventDto clone() throws CloneNotSupportedException {
        return new EventDto(eventId, userId, entityId, eventType, operation, timestamp);
    }
}
