package ru.yandex.practicum.filmorate.tool;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StringToJsonConverter {

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
