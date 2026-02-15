package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static ru.yandex.practicum.filmorate.TestStubs.*;

public class UserTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Correct user validation")
    public void valid_user_noException() {
        Set<ConstraintViolation<User>> violations = validator.validate(VALID_USER_1.clone());
        Assertions.assertTrue(violations.isEmpty(), "That was valid user!");
    }

    @Test
    @DisplayName("User with null email validation")
    public void user_null_email_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_NULL_EMAIL.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null email");
    }

    @Test
    @DisplayName("User with null login validation")
    public void user_null_login_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_NULL_LOGIN.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null login");
    }

    @Test
    @DisplayName("User with null birthday validation")
    public void user_null_birthday_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_NULL_BIRTHDAY.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on null birthday");
    }

    @Test
    @DisplayName("User with blank login validation")
    public void user_blank_login_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_LOGIN_BLANK.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on blank login");
    }

    @Test
    @DisplayName("User with empty login validation")
    public void user_empty_login_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_LOGIN_EMPTY.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on empty email");
    }

    @Test
    @DisplayName("User with email empty before at validation")
    public void user_email_empty_before_at_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_EMAIL_EMPTY_BEFORE_AT.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on email empty before at");
    }

    @Test
    @DisplayName("User with email empty after at validation")
    public void user_email_empty_after_at_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_EMAIL_EMPTY_AFTER_AT.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on email empty after at");
    }

    @Test
    @DisplayName("User with not email at email field validation")
    public void user_not_email_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_EMAIL_NOT_EMAIL.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on not an email at email field");
    }

    @Test
    @DisplayName("User with future birthday validation")
    public void user_future_birthday_hasViolation() {
        Set<ConstraintViolation<User>> violations = validator.validate(INVALID_USER_FUTURE_BIRTHDAY.clone());
        Assertions.assertFalse(violations.isEmpty(), "Expected error on future birthday");
    }
}
