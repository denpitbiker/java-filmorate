package ru.yandex.practicum.filmorate.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validation.annotation.DateInRange;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateInRangeValidator implements ConstraintValidator<DateInRange, Date> {
    private static final String PARSE_FORMAT_ERR_LOG_MSG = "Failed to provided date format from the annotation: {}";
    private static final String PARSE_START_DATE_ERR_LOG_MSG = "Failed to parse startDate from the annotation: {}";
    private static final String PARSE_END_DATE_ERR_LOG_MSG = "Failed to parse endDate from the annotation: {}";

    private Date startDate;
    private Date endDate;

    @Override
    public void initialize(DateInRange constraintAnnotation) {
        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat(constraintAnnotation.dateFormat());
        } catch (IllegalArgumentException e) {
            log.error(PARSE_FORMAT_ERR_LOG_MSG, constraintAnnotation.dateFormat());
            throw e;
        }
        if (!constraintAnnotation.startDate().isEmpty()) {
            try {
                startDate = formatter.parse(constraintAnnotation.startDate());
            } catch (ParseException e) {
                log.error(PARSE_START_DATE_ERR_LOG_MSG, constraintAnnotation.startDate());
                throw new RuntimeException(e);
            }
        }
        if (!constraintAnnotation.endDate().isEmpty()) {
            try {
                endDate = formatter.parse(constraintAnnotation.endDate());
            } catch (ParseException e) {
                log.error(PARSE_END_DATE_ERR_LOG_MSG, constraintAnnotation.endDate());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        return value != null &&
                (startDate == null || !value.before(startDate)) && (endDate == null || !value.after(endDate));
    }
}

