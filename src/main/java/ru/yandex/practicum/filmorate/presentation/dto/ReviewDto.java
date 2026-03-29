package ru.yandex.practicum.filmorate.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto implements Cloneable {
    @JsonProperty("reviewId")
    Long id;
    @NotBlank
    @Length(max = 2000, message = "Description must be <= 2000 symbols!")
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
    Integer useful;

    @Override
    public ReviewDto clone() {
        return new ReviewDto(id, content, isPositive, userId, filmId, useful);
    }
}