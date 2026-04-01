package ru.yandex.practicum.filmorate.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Director implements Cloneable {
    private Long id;

    private String name;

    @Override
    public Director clone() {
        return new Director(id, name);
    }
}
