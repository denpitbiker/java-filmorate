package ru.yandex.practicum.filmorate.data.storage;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.data.model.Event;
import ru.yandex.practicum.filmorate.data.storage.api.EventStorage;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;

import static ru.yandex.practicum.filmorate.TestStubs.VALID_EVENT_ADD_FRIEND;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_EVENT_LIKE_FILM;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_FILM_1;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_USER_1;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_USER_2;

public abstract class EventStorageTest {

    private final EventStorage storage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    protected static final int EXPECTED_REPOSITORY_SIZE_TWO = 2;
    protected static final int EXPECTED_REPOSITORY_SIZE_ONE = 1;
    protected static final int EXPECTED_REPOSITORY_SIZE_ZERO = 0;

    protected EventStorageTest(EventStorage eventStorage, UserStorage userStorage, FilmStorage filmStorage) {
        this.storage = eventStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @BeforeEach
    public void setUp() {
        userStorage.addUser(VALID_USER_1);
        userStorage.addUser(VALID_USER_2);
        filmStorage.addFilm(VALID_FILM_1);
    }

    @Test
    @DisplayName("Add correct event")
    public void addEvent_addCorrectEvent_eventAddedAndReturned() {
        Event event1 = VALID_EVENT_ADD_FRIEND.clone();
        Event event2 = VALID_EVENT_LIKE_FILM.clone();

        Assertions.assertNotNull(
                storage.addEvent(event1),
                "Event should be added and returned"
        );
        Assertions.assertNotNull(
                storage.addEvent(event2),
                "Second event should be added and returned"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_TWO,
                storage.getAllEvents().size(),
                "Repository size should be " + EXPECTED_REPOSITORY_SIZE_TWO
        );
    }

    @Test
    @DisplayName("Get all events returns all added events")
    public void getAllEvents_addMultipleEvents_allEventsReturned() {
        storage.addEvent(VALID_EVENT_ADD_FRIEND);
        storage.addEvent(VALID_EVENT_LIKE_FILM);

        Collection<Event> allEvents = storage.getAllEvents();

        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_TWO,
                allEvents.size(),
                "Should return all added events"
        );
    }

    @Test
    @DisplayName("Get events by existing user id")
    public void getEventsByUserId_getEventsByExistingUserId_eventsReturned() {
        storage.addEvent(VALID_EVENT_ADD_FRIEND);
        storage.addEvent(VALID_EVENT_LIKE_FILM);

        Collection<Event> userEvents = storage.getEventsByUserId(VALID_USER_1.getId());

        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_TWO,
                userEvents.size(),
                "Should return events only for specified user"
        );

        // Verify all returned events belong to the user
        Assertions.assertTrue(
                userEvents.stream().allMatch(event -> VALID_USER_1.getId().equals(event.getUserId())),
                "All returned events should belong to user " + VALID_USER_1.getId()
        );
    }

    @Test
    @DisplayName("Get events by non-existent user id returns empty collection")
    public void getEventsByUserId_getEventsByNonExistentUserId_emptyCollectionReturned() {
        storage.addEvent(VALID_EVENT_ADD_FRIEND);
        storage.addEvent(VALID_EVENT_LIKE_FILM);

        Collection<Event> userEvents = storage.getEventsByUserId(999L);

        Assertions.assertNotNull(
                userEvents,
                "Should return empty collection, not null"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                userEvents.size(),
                "Should return empty collection for non-existent user"
        );
    }

    @Test
    @DisplayName("Get events by user id with no events returns empty collection")
    public void getEventsByUserId_getUserIdWithNoEvents_emptyCollectionReturned() {
        Collection<Event> userEvents = storage.getEventsByUserId(VALID_USER_1.getId());

        Assertions.assertNotNull(
                userEvents,
                "Should return empty collection, not null"
        );
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                userEvents.size(),
                "Should return empty collection when user has no events"
        );
    }

    @Test
    @DisplayName("Add event with null fields should throw exception")
    public void addEvent_addEventWithNullUserId_throwsException() {
        Event invalidEvent = VALID_EVENT_ADD_FRIEND.clone();
        invalidEvent.setUserId(null);

        Assertions.assertThrows(
                Exception.class,
                () -> storage.addEvent(invalidEvent),
                "Adding event with null fields should throw exception"
        );
    }

    @Test
    @DisplayName("Add event should generate unique event id")
    public void addEvent_addMultipleEvents_uniqueIdsGenerated() {
        Event event1 = storage.addEvent(VALID_EVENT_ADD_FRIEND);
        Event event2 = storage.addEvent(VALID_EVENT_LIKE_FILM);

        Assertions.assertNotNull(event1.getEventId(), "Event 1 should have generated ID");
        Assertions.assertNotNull(event2.getEventId(), "Event 2 should have generated ID");
        Assertions.assertNotEquals(
                event1.getEventId(),
                event2.getEventId(),
                "Events should have unique IDs"
        );
    }

    @Test
    @DisplayName("Get all events returns empty collection when no events exist")
    public void getAllEvents_noEvents_returnsEmptyCollection() {
        Collection<Event> allEvents = storage.getAllEvents();

        Assertions.assertNotNull(allEvents, "Should return empty collection, not null");
        Assertions.assertEquals(
                EXPECTED_REPOSITORY_SIZE_ZERO,
                allEvents.size(),
                "Should return empty collection when no events exist"
        );
    }

}
