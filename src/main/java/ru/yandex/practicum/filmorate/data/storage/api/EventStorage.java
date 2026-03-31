package ru.yandex.practicum.filmorate.data.storage.api;

import java.util.Collection;

import ru.yandex.practicum.filmorate.data.model.Event;

public interface EventStorage {

    Collection<Event> getAllEvents();

    Collection<Event> getEventsByUserId(Long userId);

    Event addEvent(Event event);

}
