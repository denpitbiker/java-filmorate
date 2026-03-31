package ru.yandex.practicum.filmorate.data.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum EventOperation {

    REMOVE,
    ADD,
    UPDATE;

    private static final Map<Integer, EventOperation> mapper = new HashMap<>();

    static {
        for (EventOperation operation : values()) {
            mapper.put(operation.ordinal(), operation);
        }
    }

    public static EventOperation fromOrdinal(int ordinal) {
        return mapper.get(ordinal);
    }

}
