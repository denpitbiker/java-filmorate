package ru.yandex.practicum.filmorate.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectorDto implements Cloneable {
    private Long id;
    private String name;

    @Override
    public DirectorDto clone() {
        return new DirectorDto(id, name);
    }
}
