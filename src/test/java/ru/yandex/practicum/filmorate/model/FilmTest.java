package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public class FilmTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Correct film validation")
    public void validate_validFilm_noException() {
        Set<ConstraintViolation<Film>> violations = validator.validate(VALID_FILM_1.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was valid film!");
        violations = validator.validate(VALID_FILM_2.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was valid film too!");
    }

    @Test
    @DisplayName("Film with null name validation")
    public void validate_filmNullName_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_NULL_NAME.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null name");
    }

    @Test
    @DisplayName("Film with null description validation")
    public void validate_filmNullDescription_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_NULL_DESCRIPTION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null description");
    }

    @Test
    @DisplayName("Film with null date validation")
    public void validate_filmNullDate_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_NULL_DATE.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null date");
    }

    @Test
    @DisplayName("Film with null duration validation")
    public void validate_filmNullDuration_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_NULL_DURATION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null duration");
    }

    @Test
    @DisplayName("Film with too long description validation")
    public void validate_filmTooLongDescription_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_TOO_LONG_DESCRIPTION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on too long description");
    }

    @Test
    @DisplayName("Film too old date validation")
    public void validate_filmTooOldDate_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_TOO_OLD_DATE.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error for too old date");
    }

    @Test
    @DisplayName("Film with zero duration validation")
    public void validate_filmZeroDuration_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_DURATION_ZERO.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on zero duration");
    }

    @Test
    @DisplayName("Film with negative duration validation")
    public void validate_filmNegativeDuration_hasViolation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(INVALID_FILM_NEGATIVE_DURATION.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on negative duration");
    }
}
