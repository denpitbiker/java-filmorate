package ru.yandex.practicum.filmorate.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    Set<Long> likesIds;
    Set<Long> genresIds;

    @Override
    public Film clone() {
        Film cloned = new Film(id, name, description, releaseDate, mpaId, durationMinutes, new HashSet<>(), new HashSet<>());
        cloned.getLikesIds().addAll(likesIds);
        cloned.getGenresIds().addAll(genresIds);
        return cloned;
    }
}
