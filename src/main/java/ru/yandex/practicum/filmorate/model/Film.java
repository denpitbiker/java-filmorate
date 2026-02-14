package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.annotation.DateInRange;

import java.util.Date;

@Data
public class Film {
    Long id;
    @NotBlank(message = "Name should not be blank!")
    String name;
    @Length(max = 200, message = "Description must be <= 200 symbols!")
    String description;
    @DateInRange(startDate = "1895-12-28")
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date releaseDate;
    @Positive
    @JsonProperty("duration")
    Long durationMinutes;
}
