package ru.yandex.practicum.filmorate.data.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.data.model.Review;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.data.storage.api.ReviewStorage;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.TestStubs.*;

public abstract class ReviewStorageTest {

    private final ReviewStorage storage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    protected ReviewStorageTest(ReviewStorage storage, FilmStorage filmStorage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    private static final Long VALID_REVIEW_ID = 1L;
    private static final Long VALID_USER_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;

    @BeforeEach
    public void setUp() {
        userStorage.addUser(VALID_USER_1);
        filmStorage.addFilm(VALID_FILM_1);
    }

    @Test
    @DisplayName("Check existing review ID")
    public void hasReviewId_existingId_returnedTrue() {
        storage.addReview(VALID_REVIEW);
        assertTrue(storage.hasReviewId(VALID_REVIEW_ID), "Storage should contain the review ID");
    }

    @Test
    @DisplayName("Check non-existing review ID")
    public void hasReviewId_nonExistingId_returnedFalse() {
        assertFalse(storage.hasReviewId(NON_EXISTING_ID), "Storage should not contain an unknown ID");
    }

    @Test
    @DisplayName("Add a new rate")
    public void addRate_successfulRate_returnedTrue() {
        storage.addReview(VALID_REVIEW);
        assertTrue(storage.addRate(VALID_REVIEW_ID, VALID_USER_ID, true), "Rate should be added successfully");
    }

    @Test
    @DisplayName("Remove an existing rate")
    public void removeRate_existingRate_returnedTrue() {
        storage.addReview(VALID_REVIEW);
        storage.addRate(VALID_REVIEW_ID, VALID_USER_ID, true);
        assertTrue(storage.removeRate(VALID_REVIEW_ID, VALID_USER_ID, true), "Rate should be removed successfully");
    }

    @Test
    @DisplayName("Get an existing review")
    public void getReview_existingId_returnReview() {
        storage.addReview(VALID_REVIEW);
        Optional<Review> review = storage.getReview(VALID_REVIEW_ID);
        assertTrue(review.isPresent(), "Review should be returned for existing ID");
    }

    @Test
    @DisplayName("Update an existing review")
    public void updateReview_existingReview_updatedReview() {
        Review review = storage.addReview(VALID_REVIEW);
        review.setContent("Updated Content");
        Review updatedReview = storage.updateReview(review);
        assertEquals("Updated Content", updatedReview.getContent(), "Review should be updated");
    }

    @Test
    @DisplayName("Remove an existing review")
    public void removeReview_existingId_reviewRemoved() {
        storage.addReview(VALID_REVIEW);
        Review removedReview = storage.removeReview(VALID_REVIEW_ID);
        assertNotNull(removedReview, "Review should be removed successfully");
    }

    @Test
    @DisplayName("Get all reviews for a film")
    public void getAllReviews_forFilm_listOfReviews() {
        storage.addReview(VALID_REVIEW);
        List<Review> reviews = storage.getAllReviews(VALID_REVIEW.getFilmId(), 10);
        assertFalse(reviews.isEmpty(), "Should return reviews for the film");
    }
}
