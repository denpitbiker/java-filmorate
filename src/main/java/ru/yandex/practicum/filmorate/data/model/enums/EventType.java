package ru.yandex.practicum.filmorate.data.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum EventType {

    LIKE,
    REVIEW,
    FRIEND;

    private static final Map<Integer, EventType> mapper = new HashMap<>();

    static {
        for (EventType type : values()) {
            mapper.put(type.ordinal(), type);
        }
    }

    public static EventType fromOrdinal(int ordinal) {
        return mapper.get(ordinal);
    }

}
