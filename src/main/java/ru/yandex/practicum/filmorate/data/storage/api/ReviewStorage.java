package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    boolean hasReviewId(Long id);

    boolean hasRate(Long reviewId, Long userId, Boolean isPositive);

    boolean addRate(Long reviewId, Long userId, Boolean isPositive);

    boolean updateRate(Long reviewId, Long userId, Boolean isUseful);

    boolean removeRate(Long reviewId, Long userId, Boolean isPositive);

    Review addReview(Review newReview);

    Optional<Review> getReview(Long id);

    Review updateReview(Review updatedReview);

    Review removeReview(Long id);

    List<Review> getAllReviews(Long filmId, Integer count);
}
