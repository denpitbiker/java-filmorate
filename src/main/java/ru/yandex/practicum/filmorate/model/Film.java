package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.annotation.DateInRange;
import ru.yandex.practicum.filmorate.validation.annotation.NullOrNotBlank;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film implements Cloneable {
    Long id;
    @NotBlank(message = "Name should not be blank!")
    String name;
    @NullOrNotBlank
    @Length(max = 200, message = "Description must be <= 200 symbols!")
    String description;
    @DateInRange(startDate = "1895-12-28")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate releaseDate;
    @Positive
    @NotNull
    @JsonProperty("duration")
    Long durationMinutes;
    final Set<Long> likes = new HashSet<>();

    @Override
    public Film clone() {
        Film cloned = new Film(id, name, description, releaseDate, durationMinutes);
        cloned.getLikes().addAll(likes);
        return cloned;
    }
}
