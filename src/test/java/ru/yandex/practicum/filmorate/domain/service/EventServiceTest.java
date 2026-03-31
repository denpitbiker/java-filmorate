package ru.yandex.practicum.filmorate.domain.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.data.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.data.model.enums.EventType;
import ru.yandex.practicum.filmorate.presentation.dto.EventDto;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_FILM_DTO_1;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_REVIEW_DTO;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_USER_DTO_1;
import static ru.yandex.practicum.filmorate.TestStubs.VALID_USER_DTO_2;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EventServiceTest {

    @Autowired
    private FilmService filmService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private EventService eventService;

    FilmDto film;
    UserDto user;
    UserDto friend;
    ReviewDto review;

    @BeforeEach
    public void setUp() {
        user = userService.addUser(VALID_USER_DTO_1.clone());
        friend = userService.addUser(VALID_USER_DTO_2.clone());
        film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        review = reviewService.addReview(VALID_REVIEW_DTO.clone());
    }

    @Test
    @DisplayName("Get all events does not throw exception")
    public void getAllEvents_eventsList() {
        Assertions.assertDoesNotThrow(
                () -> eventService.getAllEvents(),
                "Get all events should not throw exceptions"
        );
    }

    @Test
    @DisplayName("Create add friend event")
    public void createAddFriendEvent_eventCreated() {
        Assertions.assertDoesNotThrow(
                () -> eventService.createAddFriendEvent(user.getId(), friend.getId()),
                "Create new event should not throw exceptions"
        );
        List<EventDto> events = (List<EventDto>) eventService.getEventsForUser(user.getId());
        assertEquals(1, events.size(), "Get events should return 1 event");
        assertEquals(1, events.get(0).getUserId(), "Event must have valid user id");
        assertEquals(EventType.FRIEND.name(), events.get(0).getEventType(), "Event must have valid event type");
        assertEquals(EventOperation.ADD.name(), events.get(0).getOperation(), "Event must have valid event operation");
    }

    @Test
    @DisplayName("Create remove friend event")
    public void createRemoveFriendEvent_eventCreated() {
        Assertions.assertDoesNotThrow(
                () -> eventService.createRemoveFriendEvent(user.getId(), friend.getId()),
                "Create new event should not throw exceptions"
        );
        List<EventDto> events = (List<EventDto>) eventService.getEventsForUser(user.getId());
        assertEquals(1, events.size(), "Get events should return 1 event");
        assertEquals(1, events.get(0).getUserId(), "Event must have valid user id");
        assertEquals(EventType.FRIEND.name(), events.get(0).getEventType(), "Event must have valid event type");
        assertEquals(EventOperation.REMOVE.name(), events.get(0).getOperation(), "Event must have valid event operation");
    }

    @Test
    @DisplayName("Create like film event")
    public void createLikeFilmEvent_eventCreated() {
        Assertions.assertDoesNotThrow(
                () -> eventService.createLikeFilmEvent(user.getId(), film.getId()),
                "Create like film event should not throw exceptions"
        );
        List<EventDto> events = (List<EventDto>) eventService.getEventsForUser(user.getId());
        assertEquals(1, events.size(), "Get events should return 1 event");
        EventDto event = events.get(0);
        assertEquals(user.getId(), event.getUserId(), "User ID should match");
        assertEquals(EventType.LIKE.name(), event.getEventType(), "Event type should be LIKE");
        assertEquals(EventOperation.ADD.name(), event.getOperation(), "Operation should be ADD");
    }

    @Test
    @DisplayName("Create unlike film event")
    public void createUnlikeFilmEvent_eventCreated() {
        Assertions.assertDoesNotThrow(
                () -> eventService.createUnlikeFilmEvent(user.getId(), film.getId()),
                "Create like film event should not throw exceptions"
        );
        List<EventDto> events = (List<EventDto>) eventService.getEventsForUser(user.getId());
        assertEquals(1, events.size(), "Get events should return 1 event");
        EventDto event = events.get(0);
        assertEquals(user.getId(), event.getUserId(), "User ID should match");
        assertEquals(EventType.LIKE.name(), event.getEventType(), "Event type should be LIKE");
        assertEquals(EventOperation.REMOVE.name(), event.getOperation(), "Operation should be REMOVE");
    }

    @Test
    @DisplayName("Create add review event")
    public void createAddReviewEvent_eventCreated() {
        Assertions.assertDoesNotThrow(
                () -> eventService.createAddReviewEvent(user.getId(), review.getId()),
                "Create like film event should not throw exceptions"
        );
        List<EventDto> events = (List<EventDto>) eventService.getEventsForUser(user.getId());
        assertEquals(1, events.size(), "Get events should return 1 event");
        EventDto event = events.get(0);
        assertEquals(user.getId(), event.getUserId(), "User ID should match");
        assertEquals(EventType.REVIEW.name(), event.getEventType(), "Event type should be REVIEW");
        assertEquals(EventOperation.ADD.name(), event.getOperation(), "Operation should be ADD");
    }

    @Test
    @DisplayName("Create update review event")
    public void createUpdateReviewEvent_eventCreated() {
        Assertions.assertDoesNotThrow(
                () -> eventService.createUpdateReviewEvent(user.getId(), review.getId()),
                "Create like film event should not throw exceptions"
        );
        List<EventDto> events = (List<EventDto>) eventService.getEventsForUser(user.getId());
        assertEquals(1, events.size(), "Get events should return 1 event");
        EventDto event = events.get(0);
        assertEquals(user.getId(), event.getUserId(), "User ID should match");
        assertEquals(EventType.REVIEW.name(), event.getEventType(), "Event type should be REVIEW");
        assertEquals(EventOperation.UPDATE.name(), event.getOperation(), "Operation should be UPDATE");
    }

    @Test
    @DisplayName("Create remove review event")
    public void createRemoveReviewEvent_eventCreated() {
        Assertions.assertDoesNotThrow(
                () -> eventService.createRemoveReviewEvent(user.getId(), review.getId()),
                "Create like film event should not throw exceptions"
        );
        List<EventDto> events = (List<EventDto>) eventService.getEventsForUser(user.getId());
        assertEquals(1, events.size(), "Get events should return 1 event");
        EventDto event = events.get(0);
        assertEquals(user.getId(), event.getUserId(), "User ID should match");
        assertEquals(EventType.REVIEW.name(), event.getEventType(), "Event type should be REVIEW");
        assertEquals(EventOperation.REMOVE.name(), event.getOperation(), "Operation should be REMOVE");
    }

}
