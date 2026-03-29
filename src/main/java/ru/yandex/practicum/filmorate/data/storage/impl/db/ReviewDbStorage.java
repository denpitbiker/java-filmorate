package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Review;
import ru.yandex.practicum.filmorate.data.storage.api.ReviewStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private static final String ADD_RATE_QUERY = "INSERT INTO review_rate (review_id, user_id, is_useful) VALUES (?, ?, ?)";
    private static final String DELETE_RATE_QUERY = "DELETE FROM review_rate WHERE review_id = ? AND user_id = ? AND is_useful = ?";
    private static final String ADD_REVIEW_QUERY = "INSERT INTO review (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE review SET content = ?, is_positive = ?, user_id = ?, film_id = ? WHERE id = ?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM review WHERE id=?";

    private static final String GET_REVIEW_QUERY = """
            SELECT r.id AS id, r.content AS content, r.is_positive AS is_positive, r.user_id AS user_id, r.film_id AS film_id, COALESCE(rr.rate, 0) AS rate
            FROM review AS r
            LEFT JOIN (
                SELECT
                    film_id,
                    SUM(
                      CASE
                        WHEN is_useful = true THEN 1
                        ELSE -1
                      END
                    ) AS rate
                FROM review_rate
                WHERE review_id = ?
            ) rr ON r.id = rr.review_id
            WHERE r.review_id = ?
            """;
    private static final String GET_ALL_REVIEWS_QUERY = """
            SELECT r.id AS id, r.content AS content, r.is_positive AS is_positive, r.user_id AS user_id, r.film_id AS film_id, COALESCE(rr.rate, 0) AS rate
            FROM review AS r
            LEFT JOIN (
                SELECT
                    film_id,
                    SUM(
                      CASE
                        WHEN is_useful = true THEN 1
                        ELSE -1
                      END
                    ) AS rate
                FROM review_rate
            ) rr ON r.id = rr.review_id
            WHERE ? IS NULL OR r.film_id = ?
            ORDER BY rate DESC
            LIMIT ?
            """;

    private static final String ID_COLUMN = "id";

    private static final String GET_REVIEWS_LOG = "Searching for reviews";
    private static final String GET_REVIEW_FAILED_LOG = "Failed to find review with id = {}";

    private final JdbcTemplate jdbc;

    private static final ReviewRowMapper mapper = new ReviewRowMapper();

    @Override
    public boolean hasReviewId(Long id) {
        if (id == null) return false;
        return !jdbc.queryForList(GET_REVIEW_QUERY, id).isEmpty();
    }

    @Override
    public boolean addRate(Long reviewId, Long userId, Boolean isUseful) {
        int rowsAffected = jdbc.update(ADD_RATE_QUERY, reviewId, userId, isUseful);
        return rowsAffected > 0;
    }

    @Override
    public boolean removeRate(Long reviewId, Long userId, Boolean isUseful) {
        int rowsAffected = jdbc.update(DELETE_RATE_QUERY, reviewId, userId, isUseful);
        return rowsAffected > 0;
    }

    @Override
    @Transactional
    public Review addReview(Review newReview) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_REVIEW_QUERY, new String[]{ID_COLUMN});
            ps.setString(1, newReview.getContent());
            ps.setBoolean(2, newReview.getIsPositive());
            ps.setLong(3, newReview.getUserId());
            ps.setLong(4, newReview.getFilmId());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() == null) return null;
        long userId = keyHolder.getKey().longValue();
        newReview.setId(userId);
        return newReview;
    }

    @Override
    @Transactional
    public Optional<Review> getReview(Long id) {
        try {
            Review review = jdbc.queryForObject(GET_REVIEW_QUERY, mapper, id, id);
            if (review == null) return Optional.empty();
            return Optional.of(review);
        } catch (EmptyResultDataAccessException e) {
            log.info(GET_REVIEW_FAILED_LOG, id);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Review updateReview(Review updatedReview) {
        Long userId = updatedReview.getId();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE_REVIEW_QUERY);
            ps.setString(1, updatedReview.getContent());
            ps.setBoolean(2, updatedReview.getIsPositive());
            ps.setLong(3, updatedReview.getUserId());
            ps.setLong(4, updatedReview.getFilmId());
            ps.setLong(5, userId);
            return ps;
        });
        return updatedReview;
    }

    @Override
    @Transactional
    public Review removeReview(Long id) {
        Review removed = getReview(id).orElse(null);
        if (removed != null) {
            jdbc.update(DELETE_REVIEW_QUERY, id);
        }
        return removed;
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Integer count) {
        log.trace(GET_REVIEWS_LOG);
        return jdbc.query(GET_ALL_REVIEWS_QUERY, mapper, filmId, filmId, count).stream()
                .toList();
    }
}
