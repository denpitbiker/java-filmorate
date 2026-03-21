package ru.yandex.practicum.filmorate.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film implements Cloneable {
    Long id;
    String name;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    Long mpaId;
    Long durationMinutes;

    @Override
    public Film clone() {
        return new Film(id, name, description, releaseDate, mpaId, durationMinutes);
    }
}
