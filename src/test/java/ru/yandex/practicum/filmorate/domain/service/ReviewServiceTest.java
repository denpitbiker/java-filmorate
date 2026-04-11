package ru.yandex.practicum.filmorate.domain.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import static ru.yandex.practicum.filmorate.TestStubs.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = FilmorateApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReviewServiceTest {
    private static final int TOP_ONE_COUNT = 1;
    private static final String NOT_FOUND_FILM_FAIL_MSG = "NotFoundException should be thrown for unknown film";

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    FilmDto film;
    UserDto user;
    ReviewDto review;

    @BeforeEach
    public void setUp() {
        user = userService.addUser(VALID_USER_DTO_1.clone());
        film = filmService.addFilm(VALID_FILM_DTO_1.clone());
        review = reviewService.addReview(VALID_REVIEW_DTO.clone());
    }

    @Test
    @DisplayName("Like a review")
    public void likeReview_validReviewAndUser_ReviewIsLiked() {
        Assertions.assertDoesNotThrow(
                () -> reviewService.addDislike(review.getId(), user.getId()),
                "Disliking the review should not throw exceptions"
        );
        Assertions.assertDoesNotThrow(
                () -> reviewService.addLike(review.getId(), user.getId()),
                "Liking the review should not throw exceptions"
        );
    }

    @Test
    @DisplayName("Dislike a review")
    public void dislikeReview_validFilmAndUser_ReviewIsDisliked() {
        Assertions.assertDoesNotThrow(
                () -> reviewService.addDislike(review.getId(), user.getId()),
                "Disliking the review should not throw exceptions"
        );
        Assertions.assertDoesNotThrow(
                () -> reviewService.addDislike(review.getId(), user.getId()),
                "Disliking the review should not throw exceptions"
        );
    }

    @Test
    @DisplayName("Remove like from review")
    public void removeLikeReview_validReviewAndUser_ReviewIsLiked() {
        reviewService.addLike(review.getId(), user.getId());
        Assertions.assertDoesNotThrow(
                () -> reviewService.removeLike(review.getId(), user.getId()),
                "Remove like the review should not throw exceptions"
        );
        Assertions.assertDoesNotThrow(
                () -> reviewService.removeLike(review.getId(), user.getId()),
                "Remove like the review should not throw exceptions"
        );
    }

    @Test
    @DisplayName("Remove dislike from review")
    public void removeDislikeReview_validFilmAndUser_ReviewIsDisliked() {
        reviewService.addDislike(review.getId(), user.getId());
        Assertions.assertDoesNotThrow(
                () -> reviewService.removeDislike(review.getId(), user.getId()),
                "Remove dislike from review should not throw exceptions"
        );
        Assertions.assertDoesNotThrow(
                () -> reviewService.removeLike(review.getId(), user.getId()),
                "Remove like the review should not throw exceptions"
        );
    }

    @Test
    @DisplayName("Get reviews top with negative count")
    public void getAllReviews_negativeCount_throwsValidationException() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> reviewService.getAllReviews(null, -1),
                "Negative count should throw ValidationException"
        );
    }

    @Test
    @DisplayName("Get top 1 review")
    public void getAllReviews_getTop1Review_returned1Review() {
        reviewService.addLike(review.getId(), user.getId());
        Assertions.assertDoesNotThrow(
                () -> reviewService.getAllReviews(null, TOP_ONE_COUNT),
                "Top 1 reviews should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Get top 1 review with filmId")
    public void getAllReviews_getTop1ReviewWithFilmId_returned1Review() {
        reviewService.addLike(review.getId(), user.getId());
        Assertions.assertDoesNotThrow(
                () -> reviewService.getAllReviews(film.getId(), TOP_ONE_COUNT),
                "Top 1 reviews should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Get review by id")
    public void getReview_getExistingReviewById_returnedReview() {
        Assertions.assertDoesNotThrow(
                () -> reviewService.getReview(review.getId()),
                "Review should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Get non-existing review")
    public void getReview_getNonExistingReview_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> reviewService.getReview(NON_EXISTING_ID),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Delete review by id")
    public void getReview_deleteExistingReviewById_returnedReview() {
        Assertions.assertDoesNotThrow(
                () -> reviewService.deleteReview(review.getId()),
                "Review should be returned without exceptions"
        );
    }

    @Test
    @DisplayName("Delete non-existing review")
    public void deleteReview_deleteNonExistingReview_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> reviewService.deleteReview(NON_EXISTING_ID),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }

    @Test
    @DisplayName("Add review")
    public void addReview_addNewReview_reviewAddedNoExceptions() {
        Assertions.assertDoesNotThrow(
                () -> reviewService.addReview(VALID_REVIEW_DTO.clone()),
                "Review should be added without exceptions"
        );
    }

    @Test
    @DisplayName("Add duplicate review")
    public void addReview_addExistingReview_throwDuplicatedDataException() {
        Assertions.assertThrows(
                DuplicatedDataException.class,
                () -> reviewService.addReview(review),
                "DuplicatedDataException should be thrown for duplicate review"
        );
    }

    @Test
    @DisplayName("Update review")
    public void updateReview_updateExistingReview_reviewUpdatedNoExceptions() {
        review.setContent(VALID_FILM_DESCRIPTION_2);
        Assertions.assertDoesNotThrow(
                () -> reviewService.updateReview(review),
                "Review should be updated without exceptions"
        );
    }

    @Test
    @DisplayName("Update non-existing review")
    public void updateReview_updateNonExistingReview_throwNotFoundException() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> reviewService.updateReview(VALID_REVIEW_DTO.clone()),
                NOT_FOUND_FILM_FAIL_MSG
        );
    }
}

