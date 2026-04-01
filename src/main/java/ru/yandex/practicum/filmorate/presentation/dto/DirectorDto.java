package ru.yandex.practicum.filmorate.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorDto implements Cloneable {
    private Long id;
    @NotNull
    private String name;

    @Override
    public DirectorDto clone() {
        return new DirectorDto(id, name);
    }
}
