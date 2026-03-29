package ru.yandex.practicum.filmorate.domain.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.model.Review;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.data.storage.api.ReviewStorage;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.domain.mapper.ReviewToReviewDtoMapper;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private static final String GET_REVIEW_LOG_MSG = "Get review {}";
    private static final String DELETE_REVIEW_LOG_MSG = "Delete review {}";
    private static final String ADD_LIKE_LOG_MSG = "Add like {} to review {}";
    private static final String REMOVE_LIKE_LOG_MSG = "Remove like {} from review {}";
    private static final String ADD_DISLIKE_LOG_MSG = "Add dislike {} to review {}";
    private static final String REMOVE_DISLIKE_LOG_MSG = "Remove dislike {} from review {}";
    private static final String ADDED_LIKE_LOG_MSG = "Is success add like to review {} by user {}: {}";
    private static final String ADDED_DISLIKE_LOG_MSG = "Is success add dislike to review {} by user {}: {}";
    private static final String REMOVED_LIKE_LOG_MSG = "Is success remove like from review {} by user {}: {}";
    private static final String REMOVED_DISLIKE_LOG_MSG = "Is success remove dislike from review {} by user {}: {}";
    private static final String GET_REVIEWS_LOG_MSG = "Get all reviews request for filmId {} and count {}";
    private static final String ADD_REVIEW_LOG_MSG = "Add new review request {}";
    private static final String UPDATE_REVIEW_LOG_MSG = "Update review request {}";
    private static final String REVIEW_NOT_FOUND_TRACE_MSG = "Can't find review with id: {}";
    private static final String DUPLICATE_REVIEW_FOUND_TRACE_MSG = "Already have review with id: {}";
    private static final String USER_NOT_FOUND_TRACE_MSG = "Can't find user with id: {}";
    private static final String FILM_NOT_FOUND_TRACE_MSG = "Can't find film with id: {}";

    private static final String REVIEW_NOT_FOUND_ERR_MSG = "Can't find review with id = ";
    private static final String DUPLICATE_REVIEW_ERR_MSG = "Review already exists with id = ";

    private static final String FILM_NOT_FOUND_ERR_MSG = "Can't find film with id = ";
    private static final String USER_NOT_FOUND_ERR_MSG = "Can't find user with id = ";
    private static final String REVIEWS_COUNT_ERR_MSG = "Reviews count must be positive number!";

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private static final ReviewToReviewDtoMapper reviewMapper = new ReviewToReviewDtoMapper();

    public void addLike(Long id, Long userId) {
        log.info(ADD_LIKE_LOG_MSG, userId, id);
        checkReviewIdExist(id);
        checkUserIdExist(userId);
        boolean isSuccess;
        if (checkDislikeExist(id, userId)) {
            isSuccess = reviewStorage.updateRate(id, userId, true);
        } else {
            isSuccess = reviewStorage.addRate(id, userId, true);
        }
        log.info(ADDED_LIKE_LOG_MSG, id, userId, isSuccess);
    }

    public void addDislike(Long id, Long userId) {
        log.info(ADD_DISLIKE_LOG_MSG, userId, id);
        checkReviewIdExist(id);
        checkUserIdExist(userId);
        boolean isSuccess;
        if (checkLikeExist(id, userId)) {
            isSuccess = reviewStorage.updateRate(id, userId, false);
        } else {
            isSuccess = reviewStorage.addRate(id, userId, false);
        }
        log.info(ADDED_DISLIKE_LOG_MSG, id, userId, isSuccess);
    }

    public void removeLike(Long id, Long userId) {
        log.info(REMOVE_LIKE_LOG_MSG, userId, id);
        checkReviewIdExist(id);
        checkUserIdExist(userId);
        boolean isSuccess = reviewStorage.removeRate(id, userId, true);
        log.info(REMOVED_LIKE_LOG_MSG, id, userId, isSuccess);
    }

    public void removeDislike(Long id, Long userId) {
        log.info(REMOVE_DISLIKE_LOG_MSG, userId, id);
        checkReviewIdExist(id);
        checkUserIdExist(userId);
        boolean isSuccess = reviewStorage.removeRate(id, userId, false);
        log.info(REMOVED_DISLIKE_LOG_MSG, id, userId, isSuccess);
    }

    public Collection<ReviewDto> getAllReviews(Long filmId, Integer count) {
        log.info(GET_REVIEWS_LOG_MSG, filmId, count);
        checkFilmIdExist(filmId);
        if (count <= 0) throw new ValidationException(REVIEWS_COUNT_ERR_MSG);
        return reviewStorage.getAllReviews(filmId, count).stream()
                .map(reviewMapper::toPresentation)
                .toList();
    }

    public ReviewDto getReview(Long id) {
        log.info(GET_REVIEW_LOG_MSG, id);
        return getReviewDtoOrThrow(id);
    }

    public ReviewDto deleteReview(Long id) {
        log.info(DELETE_REVIEW_LOG_MSG, id);
        Review removed = reviewStorage.removeReview(id);
        if (removed == null) throw new NotFoundException(REVIEW_NOT_FOUND_ERR_MSG + id);
        return reviewMapper.toPresentation(removed);
    }

    public ReviewDto addReview(ReviewDto newReview) {
        log.info(ADD_REVIEW_LOG_MSG, newReview);
        checkReviewIdNotExist(newReview.getId());
        checkFilmIdExist(newReview.getFilmId());
        checkUserIdExist(newReview.getUserId());
        return reviewMapper.toPresentation(reviewStorage.addReview(reviewMapper.toData(newReview)));
    }

    public ReviewDto updateReview(ReviewDto updatedReview) {
        log.info(UPDATE_REVIEW_LOG_MSG, updatedReview);
        checkReviewIdExist(updatedReview.getId());
        checkFilmIdExist(updatedReview.getFilmId());
        checkUserIdExist(updatedReview.getUserId());
        return reviewMapper.toPresentation(reviewStorage.updateReview(reviewMapper.toData(updatedReview)));
    }

    private Review getReviewOrThrow(Long id) {
        return reviewStorage.getReview(id)
                .orElseThrow(() -> new NotFoundException(REVIEW_NOT_FOUND_ERR_MSG + id));
    }

    private ReviewDto getReviewDtoOrThrow(Long id) {
        return reviewMapper.toPresentation(getReviewOrThrow(id));
    }

    private boolean checkLikeExist(Long reviewId, Long userId) {
        return reviewStorage.hasRate(reviewId, userId, true);
    }

    private boolean checkDislikeExist(Long reviewId, Long userId) {
        return reviewStorage.hasRate(reviewId, userId, false);
    }

    private void checkUserIdExist(Long id) {
        if (!userStorage.hasUserId(id)) {
            log.trace(USER_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(USER_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkFilmIdExist(Long id) {
        if (!filmStorage.hasFilmId(id)) {
            log.trace(FILM_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(FILM_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkReviewIdExist(Long id) {
        if (!reviewStorage.hasReviewId(id)) {
            log.trace(REVIEW_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(REVIEW_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkReviewIdNotExist(Long id) {
        if (reviewStorage.hasReviewId(id)) {
            log.trace(DUPLICATE_REVIEW_FOUND_TRACE_MSG, id);
            throw new DuplicatedDataException(DUPLICATE_REVIEW_ERR_MSG + id);
        }
    }
}
