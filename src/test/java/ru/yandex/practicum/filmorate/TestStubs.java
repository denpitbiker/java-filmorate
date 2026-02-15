package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class TestStubs {
    public static final String VALID_FILM_NAME_1 = "Крепкий орешек";
    public static final String VALID_FILM_NAME_2 = "Бесславные ублюдки";
    public static final String VALID_FILM_DESCRIPTION_1 = "Базовый фильм";
    public static final String VALID_FILM_DESCRIPTION_2 = "Вторая мировая война. В оккупированной немцами Франции группа американских солдат-евреев наводит страх на нацистов, жестоко убивая и скальпируя солдат.";
    public static final String INVALID_FILM_DESCRIPTION_TOO_LONG = """
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            Съешь ещё этих мягких французских булок, да выпей чаю
            """;
    public final static LocalDate VALID_DATE_1 = LocalDate.of(1988, 8, 12);
    public final static LocalDate VALID_DATE_2 = LocalDate.of(2009, 5, 20);
    public final static LocalDate TOOL_OLD_DATE = LocalDate.of(1840, 5, 20);
    public final static Long VALID_DURATION_1 = 133L;
    public final static Long VALID_DURATION_2 = 153L;
    public final static Long INVALID_DURATION_ZERO = 0L;
    public final static Long INVALID_DURATION_NEGATIVE = -1240L;

    public static final Film VALID_FILM_1 = new Film(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, VALID_DURATION_1);
    public static final Film VALID_FILM_2 = new Film(null, VALID_FILM_NAME_2, VALID_FILM_DESCRIPTION_2, VALID_DATE_2, VALID_DURATION_2);

    public static final Film INVALID_FILM_NULL_NAME = new Film(null, null, VALID_FILM_DESCRIPTION_2, VALID_DATE_1, VALID_DURATION_2);
    public static final Film INVALID_FILM_NULL_DESCRIPTION = new Film(null, VALID_FILM_NAME_1, null, VALID_DATE_1, VALID_DURATION_1);
    public static final Film INVALID_FILM_NULL_DATE = new Film(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, null, VALID_DURATION_1);
    public static final Film INVALID_FILM_NULL_DURATION = new Film(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, null);
    public static final Film INVALID_FILM_TOO_LONG_DESCRIPTION = new Film(null, VALID_FILM_NAME_1, INVALID_FILM_DESCRIPTION_TOO_LONG, VALID_DATE_1, VALID_DURATION_2);
    public static final Film INVALID_FILM_TOO_OLD_DATE = new Film(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, TOOL_OLD_DATE, VALID_DURATION_2);
    public static final Film INVALID_FILM_DURATION_ZERO = new Film(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, INVALID_DURATION_ZERO);
    public static final Film INVALID_FILM_NEGATIVE_DURATION = new Film(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, INVALID_DURATION_NEGATIVE);

    public static final String VALID_LOGIN_1 = "vdenk";
    public static final String VALID_LOGIN_2 = "qreqwrew";
    public static final String VALID_USER_NAME_1 = "Denis";
    public static final String VALID_USER_NAME_2 = "Lera";
    public static final String INVALID_LOGIN_BLANK = "      ";
    public static final String INVALID_LOGIN_EMPTY = "";
    public static final String VALID_EMAIL_1 = "a.bcd@gmail.com";
    public static final String VALID_EMAIL_2 = "vd@yandex.ru";
    public static final String INVALID_EMAIL_EMPTY_BEFORE_AT = "@gmail.com";
    public static final String INVALID_EMAIL_EMPTY_AFTER_AT = "gmail.com@";
    public static final String INVALID_EMAIL_NOT_EMAIL = "fasnflaknsflkasnfksanlfk";
    public final static LocalDate VALID_BIRTHDAY_1 = LocalDate.now().minusYears(10);
    public final static LocalDate VALID_BIRTHDAY_2 = LocalDate.now().minusYears(5);
    public final static LocalDate INVALID_FUTURE_BIRTHDAY = LocalDate.now().plusYears(10);

    public static final User VALID_USER_1 = new User(null, VALID_EMAIL_1, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User VALID_USER_2 = new User(null, VALID_EMAIL_2, VALID_LOGIN_2, VALID_USER_NAME_2, VALID_BIRTHDAY_2);
    public static final User INVALID_USER_NULL_EMAIL = new User(null, null, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User INVALID_USER_NULL_LOGIN = new User(null, VALID_EMAIL_1, null, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User INVALID_USER_NULL_BIRTHDAY = new User(null, VALID_EMAIL_1, VALID_LOGIN_1, VALID_USER_NAME_1, null);
    public static final User INVALID_USER_LOGIN_BLANK = new User(null, VALID_EMAIL_1, INVALID_LOGIN_BLANK, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User INVALID_USER_LOGIN_EMPTY = new User(null, VALID_EMAIL_1, INVALID_LOGIN_EMPTY, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User INVALID_USER_EMAIL_EMPTY_BEFORE_AT = new User(null, INVALID_EMAIL_EMPTY_BEFORE_AT, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User INVALID_USER_EMAIL_EMPTY_AFTER_AT = new User(null, INVALID_EMAIL_EMPTY_AFTER_AT, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User INVALID_USER_EMAIL_NOT_EMAIL = new User(null, INVALID_EMAIL_NOT_EMAIL, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User INVALID_USER_FUTURE_BIRTHDAY = new User(null, VALID_EMAIL_1, VALID_LOGIN_1, VALID_USER_NAME_1, INVALID_FUTURE_BIRTHDAY);
}
