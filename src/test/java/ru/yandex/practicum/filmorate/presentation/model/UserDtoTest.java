package ru.yandex.practicum.filmorate.presentation.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.util.Set;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public class UserDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Correct user validation")
    public void validate_validUserDto_noException() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(VALID_USER_DTO_1.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was valid user!");
    }

    @Test
    @DisplayName("UserDto with null email validation")
    public void validate_userNullEmail_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_NULL_EMAIL.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null email");
    }

    @Test
    @DisplayName("UserDto with null login validation")
    public void validate_userNullLogin_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_NULL_LOGIN.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null login");
    }

    @Test
    @DisplayName("UserDto with null birthday validation")
    public void validate_userNullBirthday_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_NULL_BIRTHDAY.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null birthday");
    }

    @Test
    @DisplayName("UserDto with blank login validation")
    public void validate_userBlankLogin_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_LOGIN_BLANK.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on blank login");
    }

    @Test
    @DisplayName("UserDto with empty login validation")
    public void validate_userEmptyLogin_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_LOGIN_EMPTY.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on empty email");
    }

    @Test
    @DisplayName("UserDto with email empty before at validation")
    public void validate_userEmailEmptyBeforeAt_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_EMAIL_EMPTY_BEFORE_AT.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on email empty before at");
    }

    @Test
    @DisplayName("UserDto with email empty after at validation")
    public void validate_userEmailEmptyAfterAt_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_EMAIL_EMPTY_AFTER_AT.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on email empty after at");
    }

    @Test
    @DisplayName("UserDto with invalid email field validation")
    public void validate_userInvalidEmail_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_INVALID_EMAIL.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on invalid email field");
    }

    @Test
    @DisplayName("UserDto with future birthday validation")
    public void validate_userFutureBirthday_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_FUTURE_BIRTHDAY.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on future birthday");
    }
}
