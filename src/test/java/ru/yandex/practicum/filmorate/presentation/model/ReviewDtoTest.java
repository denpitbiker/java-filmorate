package ru.yandex.practicum.filmorate.presentation.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;

import java.util.Set;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public class ReviewDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Correct review validation")
    public void validate_validReviewDto_noViolation() {
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(VALID_REVIEW_DTO.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was a valid review!");
    }

    @Test
    @DisplayName("ReviewDto with null content validation")
    public void validate_reviewNullContent_hasViolation() {
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(INVALID_REVIEW_DTO_NULL_CONTENT.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null content");
    }

    @Test
    @DisplayName("ReviewDto with blank content validation")
    public void validate_reviewBlankContent_hasViolation() {
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(INVALID_REVIEW_DTO_BLANK_CONTENT.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on blank content");
    }

    @Test
    @DisplayName("ReviewDto with too long content validation")
    public void validate_reviewTooLongContent_hasViolation() {
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(INVALID_REVIEW_DTO_TOO_LONG_CONTENT.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on too long content");
    }

    @Test
    @DisplayName("ReviewDto with null isPositive validation")
    public void validate_reviewNullIsPositive_hasViolation() {
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(INVALID_REVIEW_DTO_NULL_IS_POSITIVE.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null isPositive");
    }

    @Test
    @DisplayName("ReviewDto with null userId validation")
    public void validate_reviewNullUserId_hasViolation() {
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(INVALID_REVIEW_DTO_NULL_USER_ID.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null userId");
    }

    @Test
    @DisplayName("ReviewDto with null filmId validation")
    public void validate_reviewNullFilmId_hasViolation() {
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(INVALID_REVIEW_DTO_NULL_FILM_ID.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null filmId");
    }
}
