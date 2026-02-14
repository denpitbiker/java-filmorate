package ru.yandex.practicum.filmorate.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validation.validator.DateInRangeValidator;

import java.lang.annotation.*;

@Constraint(validatedBy = DateInRangeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateInRange {

    String message() default "Date should be in valid range";

    String dateFormat() default "yyyy-MM-dd";

    /**
     * Inclusive.
     * Would be without limit on empty value.
     */
    String startDate() default "";

    /**
     * Inclusive.
     * Would be without limit on empty value.
     */
    String endDate() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

