package ru.yandex.practicum.filmorate.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validation.validator.NullOrNotBlankValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {

    String message() default "Field must be null or not blank!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

