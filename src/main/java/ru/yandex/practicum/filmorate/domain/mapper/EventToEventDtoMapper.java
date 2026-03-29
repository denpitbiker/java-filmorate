package ru.yandex.practicum.filmorate.domain.mapper;

import java.time.ZoneId;

import ru.yandex.practicum.filmorate.data.model.Event;
import ru.yandex.practicum.filmorate.presentation.dto.EventDto;

public class EventToEventDtoMapper {

    public EventDto toPresentation(Event event) {
        Long epochTime = event.getTimestamp()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        return EventDto.builder()
                .eventId(event.getEventId())
                .userId(event.getUserId())
                .entityId(event.getEntityId())
                .eventType(event.getEventType().name())
                .operation(event.getEventOperation().name())
                .timestamp(epochTime)
        .build();
    }

}
