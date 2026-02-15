package ru.yandex.practicum.filmorate.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validation.annotation.DateInRange;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class DateInRangeValidator implements ConstraintValidator<DateInRange, LocalDate> {
    private static final String PARSE_FORMAT_ERR_LOG_MSG = "Failed to provided date format from the annotation: {}";
    private static final String PARSE_START_DATE_ERR_LOG_MSG = "Failed to parse startDate from the annotation: {}";
    private static final String PARSE_END_DATE_ERR_LOG_MSG = "Failed to parse endDate from the annotation: {}";

    private LocalDate startDate;
    private LocalDate endDate;

    @Override
    public void initialize(DateInRange constraintAnnotation) {
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern(constraintAnnotation.dateFormat());
        } catch (IllegalArgumentException e) {
            log.error(PARSE_FORMAT_ERR_LOG_MSG, constraintAnnotation.dateFormat());
            throw e;
        }
        if (!constraintAnnotation.startDate().isEmpty()) {
            try {
                startDate = LocalDate.parse(constraintAnnotation.startDate(), formatter);
            } catch (DateTimeParseException e) {
                log.error(PARSE_START_DATE_ERR_LOG_MSG, constraintAnnotation.startDate());
                throw new RuntimeException(e);
            }
        }
        if (!constraintAnnotation.endDate().isEmpty()) {
            try {
                endDate = LocalDate.parse(constraintAnnotation.endDate(), formatter);
            } catch (DateTimeParseException e) {
                log.error(PARSE_END_DATE_ERR_LOG_MSG, constraintAnnotation.endDate());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null &&
                (startDate == null || !value.isBefore(startDate)) && (endDate == null || !value.isAfter(endDate));
    }
}

