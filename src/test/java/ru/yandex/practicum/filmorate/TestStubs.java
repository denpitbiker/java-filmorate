package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.data.model.Director;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;

import ru.yandex.practicum.filmorate.data.model.Event;
import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.data.model.Review;
import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.presentation.dto.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import ru.yandex.practicum.filmorate.data.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.data.model.enums.EventType;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;
import ru.yandex.practicum.filmorate.presentation.dto.MpaDto;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;
import java.util.List;

public class TestStubs {
    public static final int EXPECTED_REPOSITORY_SIZE_TWO = 2;
    public static final int EXPECTED_REPOSITORY_SIZE_ONE = 1;
    public static final int EXPECTED_REPOSITORY_SIZE_ZERO = 0;
    public static final long NON_EXISTING_ID = 314231512;

    public static final String VALID_FILM_NAME_1 = "Крепкий орешек";
    public static final String VALID_FILM_NAME_2 = "Бесславные ублюдки";
    public static final String VALID_FILM_NAME_3 = "Ждун";
    public static final String VALID_FILM_DESCRIPTION_1 = "Базовый фильм";
    public static final String VALID_FILM_DESCRIPTION_2 = "Вторая мировая война. В оккупированной немцами Франции группа американских солдат-евреев наводит страх на нацистов, жестоко убивая и скальпируя солдат.";
    public static final String VALID_FILM_DESCRIPTION_3 = "Внезапное детище российского кинопроизводства";
    public static final String INVALID_FILM_DESCRIPTION_BLANK = "       ";
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
    public static final LocalDate VALID_DATE_1 = LocalDate.of(1988, 8, 12);
    public static final LocalDate VALID_DATE_2 = LocalDate.of(2009, 5, 20);
    public static final LocalDate TOOL_OLD_DATE = LocalDate.of(1840, 5, 20);
    public static final Long VALID_DURATION_1 = 133L;
    public static final Long VALID_DURATION_2 = 153L;
    public static final Long INVALID_DURATION_ZERO = 0L;
    public static final Long INVALID_DURATION_NEGATIVE = -1240L;

    public static final Long VALID_MPA_ID = 1L;
    public static final String VALID_MPA_VALUE = "G";

    public static final Long VALID_GENRE_ID = 1L;
    public static final String VALID_GENRE_VALUE = "Комедия";

    public static final MpaDto VALID_MPA_DTO = new MpaDto(VALID_MPA_ID, VALID_MPA_VALUE);

    public static final String VALID_DIRECTOR_NAME_1 = "Quentin Tarantino";
    public static final String VALID_DIRECTOR_NAME_2 = "Woody Allen";

    public static final Director VALID_DIRECTOR_1 = new Director(1L, VALID_DIRECTOR_NAME_1);
    public static final Director VALID_DIRECTOR_2 = new Director(2L, VALID_DIRECTOR_NAME_2);

    public static final DirectorDto VALID_DIRECTOR_DTO_1 = new DirectorDto(1L, VALID_DIRECTOR_NAME_1);
    public static final DirectorDto VALID_DIRECTOR_DTO_2 = new DirectorDto(2L, VALID_DIRECTOR_NAME_2);
    public static final DirectorDto INVALID_DIRECTOR_DTO = new DirectorDto(3L, null);

    public static final FilmDto VALID_FILM_DTO_1 = new FilmDto(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, VALID_MPA_DTO, VALID_DURATION_1, new LinkedHashSet<>(), null);
    public static final FilmDto VALID_FILM_DTO_2 = new FilmDto(null, VALID_FILM_NAME_2, VALID_FILM_DESCRIPTION_2, VALID_DATE_2, VALID_MPA_DTO, VALID_DURATION_2, null, null);
    public static final FilmDto VALID_FILM_DTO_3 = new FilmDto(null, VALID_FILM_NAME_3, VALID_FILM_DESCRIPTION_3, VALID_DATE_2, VALID_MPA_DTO, VALID_DURATION_2, null, new LinkedHashSet<>(List.of(VALID_DIRECTOR_DTO_1, VALID_DIRECTOR_DTO_2)));

    public static final Film VALID_FILM_1 = new Film(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, VALID_MPA_ID, VALID_DURATION_1);
    public static final Film VALID_FILM_2 = new Film(null, VALID_FILM_NAME_2, VALID_FILM_DESCRIPTION_2, VALID_DATE_2, VALID_MPA_ID, VALID_DURATION_2);

    public static final FilmDto INVALID_FILM_DTO_NULL_NAME = new FilmDto(null, null, VALID_FILM_DESCRIPTION_2, VALID_DATE_1, VALID_MPA_DTO, VALID_DURATION_2, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_NULL_DESCRIPTION = new FilmDto(null, VALID_FILM_NAME_1, null, VALID_DATE_1, VALID_MPA_DTO, VALID_DURATION_1, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_BLANK_DESCRIPTION = new FilmDto(null, VALID_FILM_NAME_1, INVALID_FILM_DESCRIPTION_BLANK, VALID_DATE_1, VALID_MPA_DTO, VALID_DURATION_1, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_NULL_DATE = new FilmDto(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, null, VALID_MPA_DTO, VALID_DURATION_1, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_NULL_DURATION = new FilmDto(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, VALID_MPA_DTO, null, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_TOO_LONG_DESCRIPTION = new FilmDto(null, VALID_FILM_NAME_1, INVALID_FILM_DESCRIPTION_TOO_LONG, VALID_DATE_1, VALID_MPA_DTO, VALID_DURATION_2, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_TOO_OLD_DATE = new FilmDto(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, TOOL_OLD_DATE, VALID_MPA_DTO, VALID_DURATION_2, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_DURATION_ZERO = new FilmDto(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, VALID_MPA_DTO, INVALID_DURATION_ZERO, new LinkedHashSet<>(), null);
    public static final FilmDto INVALID_FILM_DTO_NEGATIVE_DURATION = new FilmDto(null, VALID_FILM_NAME_1, VALID_FILM_DESCRIPTION_1, VALID_DATE_1, VALID_MPA_DTO, INVALID_DURATION_NEGATIVE, new LinkedHashSet<>(), null);

    public static final String VALID_LOGIN_1 = "vdenk";
    public static final String VALID_LOGIN_2 = "qreqwrew";
    public static final String VALID_LOGIN_3 = "eeee";
    public static final String VALID_USER_NAME_1 = "Denis";
    public static final String VALID_USER_NAME_2 = "Lera";
    public static final String VALID_USER_NAME_3 = "Ilnur";
    public static final String INVALID_LOGIN_BLANK = "      ";
    public static final String INVALID_LOGIN_EMPTY = "";
    public static final String VALID_EMAIL_1 = "a.bcd@gmail.com";
    public static final String VALID_EMAIL_2 = "vd@yandex.ru";
    public static final String VALID_EMAIL_3 = "vde@yandex.ru";
    public static final String INVALID_EMAIL_EMPTY_BEFORE_AT = "@gmail.com";
    public static final String INVALID_EMAIL_EMPTY_AFTER_AT = "gmail.com@";
    public static final String INVALID_EMAIL = "fasnflaknsflkasnfksanlfk";
    public static final LocalDate VALID_BIRTHDAY_1 = LocalDate.now().minusYears(10);
    public static final LocalDate VALID_BIRTHDAY_2 = LocalDate.now().minusYears(5);
    public static final LocalDate VALID_BIRTHDAY_3 = LocalDate.now().minusYears(7);
    public static final LocalDate INVALID_FUTURE_BIRTHDAY = LocalDate.now().plusYears(10);

    public static final UserDto VALID_USER_DTO_1 = new UserDto(null, VALID_EMAIL_1, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto VALID_USER_DTO_2 = new UserDto(null, VALID_EMAIL_2, VALID_LOGIN_2, VALID_USER_NAME_2, VALID_BIRTHDAY_2);
    public static final UserDto VALID_USER_DTO_3 = new UserDto(null, VALID_EMAIL_3, VALID_LOGIN_3, VALID_USER_NAME_3, VALID_BIRTHDAY_3);

    public static final User VALID_USER_1 = new User(null, VALID_EMAIL_1, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final User VALID_USER_2 = new User(null, VALID_EMAIL_2, VALID_LOGIN_2, VALID_USER_NAME_2, VALID_BIRTHDAY_2);
    public static final User VALID_USER_3 = new User(null, VALID_EMAIL_3, VALID_LOGIN_3, VALID_USER_NAME_3, VALID_BIRTHDAY_3);

    public static final UserDto INVALID_USER_DTO_NULL_EMAIL = new UserDto(null, null, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto INVALID_USER_DTO_NULL_LOGIN = new UserDto(null, VALID_EMAIL_1, null, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto INVALID_USER_DTO_NULL_BIRTHDAY = new UserDto(null, VALID_EMAIL_1, VALID_LOGIN_1, VALID_USER_NAME_1, null);
    public static final UserDto INVALID_USER_DTO_LOGIN_BLANK = new UserDto(null, VALID_EMAIL_1, INVALID_LOGIN_BLANK, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto INVALID_USER_DTO_LOGIN_EMPTY = new UserDto(null, VALID_EMAIL_1, INVALID_LOGIN_EMPTY, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto INVALID_USER_DTO_EMAIL_EMPTY_BEFORE_AT = new UserDto(null, INVALID_EMAIL_EMPTY_BEFORE_AT, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto INVALID_USER_DTO_EMAIL_EMPTY_AFTER_AT = new UserDto(null, INVALID_EMAIL_EMPTY_AFTER_AT, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto INVALID_USER_DTO_INVALID_EMAIL = new UserDto(null, INVALID_EMAIL, VALID_LOGIN_1, VALID_USER_NAME_1, VALID_BIRTHDAY_1);
    public static final UserDto INVALID_USER_DTO_FUTURE_BIRTHDAY = new UserDto(null, VALID_EMAIL_1, VALID_LOGIN_1, VALID_USER_NAME_1, INVALID_FUTURE_BIRTHDAY);

    public static final ReviewDto VALID_REVIEW_DTO = new ReviewDto(null, "Great movie!", true, 1L, 1L, 10);
    public static final ReviewDto INVALID_REVIEW_DTO_NULL_CONTENT = new ReviewDto(1L, null, true, 1L, 1L, 10);
    public static final ReviewDto INVALID_REVIEW_DTO_BLANK_CONTENT = new ReviewDto(1L, "   ", true, 1L, 1L, 10);
    public static final ReviewDto INVALID_REVIEW_DTO_TOO_LONG_CONTENT = new ReviewDto(1L, "a".repeat(2001), true, 1L, 1L, 10);
    public static final ReviewDto INVALID_REVIEW_DTO_NULL_IS_POSITIVE = new ReviewDto(1L, "Nice film!", null, 1L, 1L, 10);
    public static final ReviewDto INVALID_REVIEW_DTO_NULL_USER_ID = new ReviewDto(1L, "Nice film!", true, null, 1L, 10);
    public static final ReviewDto INVALID_REVIEW_DTO_NULL_FILM_ID = new ReviewDto(1L, "Nice film!", true, 1L, null, 10);

    public static final Review VALID_REVIEW = new Review(null, "dfsfsdfsd", true, 1L, 1L, 10);

    public static final Event VALID_EVENT_ADD_FRIEND = new Event(null, 1L, 2L, EventType.FRIEND, EventOperation.ADD, LocalDateTime.now());
    public static final Event VALID_EVENT_LIKE_FILM = new Event(null, 1L, 1L, EventType.LIKE, EventOperation.ADD, LocalDateTime.now());

    public static GenreDto genre(Long id) {
        return new GenreDto(id, "test");
    }
}
