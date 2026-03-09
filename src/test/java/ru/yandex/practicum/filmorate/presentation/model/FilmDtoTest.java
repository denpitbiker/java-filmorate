package ru.yandex.practicum.filmorate.presentation.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;

import java.util.Set;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public class FilmDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Correct film validation")
    public void validate_validFilmDto_noViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(VALID_FILM_DTO_1.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was valid film!");
        violations = validator.validate(VALID_FILM_DTO_2.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was valid film too!");
    }

    @Test
    @DisplayName("FilmDto with null name validation")
    public void validate_filmNullName_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_NULL_NAME.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null name");
    }

    @Test
    @DisplayName("FilmDto with null description validation")
    public void validate_filmNullDescription_noViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_NULL_DESCRIPTION.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was valid film!");
    }

    @Test
    @DisplayName("FilmDto with blank description validation")
    public void validate_filmNullDescription_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_BLANK_DESCRIPTION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on blank description");
    }

    @Test
    @DisplayName("FilmDto with null date validation")
    public void validate_filmNullDate_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_NULL_DATE.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null date");
    }

    @Test
    @DisplayName("FilmDto with null duration validation")
    public void validate_filmNullDuration_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_NULL_DURATION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null duration");
    }

    @Test
    @DisplayName("FilmDto with too long description validation")
    public void validate_filmTooLongDescription_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_TOO_LONG_DESCRIPTION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on too long description");
    }

    @Test
    @DisplayName("FilmDto too old date validation")
    public void validate_filmTooOldDate_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_TOO_OLD_DATE.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error for too old date");
    }

    @Test
    @DisplayName("FilmDto with zero duration validation")
    public void validate_filmZeroDuration_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_DURATION_ZERO.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on zero duration");
    }

    @Test
    @DisplayName("FilmDto with negative duration validation")
    public void validate_filmNegativeDuration_hasViolation() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(INVALID_FILM_DTO_NEGATIVE_DURATION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on negative duration");
    }
}
