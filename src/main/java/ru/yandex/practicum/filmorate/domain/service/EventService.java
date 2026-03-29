package ru.yandex.practicum.filmorate.domain.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Event;
import ru.yandex.practicum.filmorate.data.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.data.model.enums.EventType;
import ru.yandex.practicum.filmorate.data.storage.api.EventStorage;
import ru.yandex.practicum.filmorate.domain.mapper.EventToEventDtoMapper;
import ru.yandex.practicum.filmorate.presentation.dto.EventDto;

@Slf4j
@Service
public class EventService {
    private static final String GET_EVENTS_LOG = "Get all events";
    private static final String GET_EVENTS_FOR_USER_LOG = "Get all events for user with id {}";
    private static final String ADD_EVENT_LOG = "Create new event {}";

    private final EventStorage eventStorage;

    private static final EventToEventDtoMapper eventMapper = new EventToEventDtoMapper();

    public EventService(@DbStorage EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public Collection<EventDto> getAllEvents() {
        log.debug(GET_EVENTS_LOG);
        return eventStorage.getAllEvents().stream()
                .map(eventMapper::toPresentation)
                .collect(Collectors.toList());
    }

    public Collection<EventDto> getEventsForUser(Long userId) {
        log.debug(GET_EVENTS_FOR_USER_LOG, userId);
        return eventStorage.getEventsByUserId(userId).stream()
                .map(eventMapper::toPresentation)
                .collect(Collectors.toList());
    }

    /**
     * Base method to create events
     * For new types and operations need add new public methods
     */
    protected void createEvent(Long userId, Long entityId, EventType type, EventOperation operation) {
        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(type)
                .eventOperation(operation)
                .timestamp(LocalDateTime.now())
                .build();
        log.debug(ADD_EVENT_LOG, event);
        eventStorage.addEvent(event);
    }

    protected void createFriendEvent(Long userId, Long friendId, EventOperation operation) {
        createEvent(userId, friendId, EventType.FRIEND, operation);
    }

    protected void createLikeEvent(Long userId, Long filmId, EventOperation operation) {
        createEvent(userId, filmId, EventType.LIKE, operation);
    }

    protected void createReviewEvent(Long userId, Long reviewId, EventOperation operation) {
        createEvent(userId, reviewId, EventType.REVIEW, operation);
    }

    public void createAddFriendEvent(Long userId, Long friendId) {
        createFriendEvent(userId, friendId, EventOperation.ADD);
    }

    public void createRemoveFriendEvent(Long userId, Long friendId) {
        createFriendEvent(userId, friendId, EventOperation.REMOVE);
    }

    public void createLikeFilmEvent(Long userId, Long filmId) {
        createLikeEvent(userId, filmId, EventOperation.ADD);
    }

    public void createUnlikeFilmEvent(Long userId, Long filmId) {
        createLikeEvent(userId, filmId, EventOperation.REMOVE);
    }

    public void createAddReviewEvent(Long userId, Long reviewId) {
        createReviewEvent(userId, reviewId, EventOperation.ADD);
    }

    public void createRemoveReviewEvent(Long userId, Long reviewId) {
        createReviewEvent(userId, reviewId, EventOperation.REMOVE);
    }

    public void createUpdateReviewEvent(Long userId, Long reviewId) {
        createReviewEvent(userId, reviewId, EventOperation.UPDATE);
    }

}
