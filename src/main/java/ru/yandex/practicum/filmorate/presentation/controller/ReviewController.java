package ru.yandex.practicum.filmorate.presentation.controller;

import java.util.Collection;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.domain.service.EventService;
import ru.yandex.practicum.filmorate.domain.service.ReviewService;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(ReviewController.CONTROLLER_ROUTE)
@RestController
public class ReviewController {
    private static final String REVIEW_ID_PATH_VAR = "id";
    private static final String USER_ID_PATH_VAR = "userId";

    public static final String COUNT_PARAM = "count";
    public static final String FILM_ID_PARAM = "filmId";

    private static final String DEFAULT_REVIEWS_RETURN_COUNT = "10";

    public static final String CONTROLLER_ROUTE = "/reviews";
    public static final String GET_REVIEW_SUBROUTE = "/{" + REVIEW_ID_PATH_VAR + "}";
    public static final String DELETE_REVIEW_SUBROUTE = "/{" + REVIEW_ID_PATH_VAR + "}";
    public static final String ADD_LIKE_SUBROUTE = "/{" + REVIEW_ID_PATH_VAR + "}/like/{" + USER_ID_PATH_VAR + "}";
    public static final String ADD_DISLIKE_SUBROUTE = "/{" + REVIEW_ID_PATH_VAR + "}/dislike/{" + USER_ID_PATH_VAR + "}";
    public static final String REMOVE_LIKE_SUBROUTE = "/{" + REVIEW_ID_PATH_VAR + "}/like/{" + USER_ID_PATH_VAR + "}";
    public static final String REMOVE_DISLIKE_SUBROUTE = "/{" + REVIEW_ID_PATH_VAR + "}/dislike/{" + USER_ID_PATH_VAR + "}";

    private static final String GET_REVIEW_LOG_MSG = "Get review {} request";
    private static final String DELETE_REVIEW_LOG_MSG = "Delete review {} request";
    private static final String ADD_LIKE_LOG_MSG = "Add like {} to review {} request";
    private static final String REMOVE_LIKE_LOG_MSG = "Remove like {} from review {} request";
    private static final String ADD_DISLIKE_LOG_MSG = "Add dislike {} to review {} request";
    private static final String REMOVE_DISLIKE_LOG_MSG = "Remove dislike {} from review {} request";
    private static final String GET_REVIEWS_LOG_MSG = "Get all reviews request for filmId {} and count {}";
    private static final String ADD_REVIEW_LOG_MSG = "Add new review request {}";
    private static final String UPDATE_REVIEW_LOG_MSG = "Update review request {}";

    private final ReviewService reviewService;
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@Valid @RequestBody ReviewDto newReview) {
        log.info(ADD_REVIEW_LOG_MSG, newReview);
        ReviewDto review = reviewService.addReview(newReview);
        eventService.createAddReviewEvent(review.getUserId(), review.getId());
        return review;
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody ReviewDto updatedReview) {
        log.info(UPDATE_REVIEW_LOG_MSG, updatedReview);
        ReviewDto review = reviewService.updateReview(updatedReview);
        eventService.createUpdateReviewEvent(review.getUserId(), review.getId());
        return review;
    }

    @DeleteMapping(DELETE_REVIEW_SUBROUTE)
    public ReviewDto deleteReview(@PathVariable(REVIEW_ID_PATH_VAR) Long id) {
        log.info(DELETE_REVIEW_LOG_MSG, id);
        ReviewDto review = reviewService.deleteReview(id);
        eventService.createRemoveReviewEvent(review.getUserId(), review.getId());
        return review;
    }

    @GetMapping(GET_REVIEW_SUBROUTE)
    public ReviewDto getReview(@PathVariable(REVIEW_ID_PATH_VAR) Long id) {
        log.info(GET_REVIEW_LOG_MSG, id);
        return reviewService.getReview(id);
    }

    @PutMapping(ADD_LIKE_SUBROUTE)
    public void addLike(@PathVariable(REVIEW_ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(ADD_LIKE_LOG_MSG, userId, id);
        reviewService.addLike(id, userId);
    }

    @PutMapping(ADD_DISLIKE_SUBROUTE)
    public void addDislike(@PathVariable(REVIEW_ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(ADD_DISLIKE_LOG_MSG, userId, id);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping(REMOVE_LIKE_SUBROUTE)
    public void removeLike(@PathVariable(REVIEW_ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(REMOVE_LIKE_LOG_MSG, userId, id);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping(REMOVE_DISLIKE_SUBROUTE)
    public void removeDislike(@PathVariable(REVIEW_ID_PATH_VAR) Long id, @PathVariable(USER_ID_PATH_VAR) Long userId) {
        log.info(REMOVE_DISLIKE_LOG_MSG, userId, id);
        reviewService.removeDislike(id, userId);
    }

    @GetMapping
    public Collection<ReviewDto> getAllReviews(
            @RequestParam(value = COUNT_PARAM, defaultValue = DEFAULT_REVIEWS_RETURN_COUNT) Integer count,
            @RequestParam(value = FILM_ID_PARAM, required = false) Long filmId
    ) {
        log.info(GET_REVIEWS_LOG_MSG, filmId, count);
        return reviewService.getAllReviews(filmId, count);
    }
}
