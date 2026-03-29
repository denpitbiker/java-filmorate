package ru.yandex.practicum.filmorate.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Review implements Cloneable {
    Long id;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Integer useful;

    @Override
    public Review clone() {
        return new Review(id, content, isPositive, userId, filmId, useful);
    }
}
