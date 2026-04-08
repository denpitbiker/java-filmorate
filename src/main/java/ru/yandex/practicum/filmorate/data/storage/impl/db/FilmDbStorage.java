package ru.yandex.practicum.filmorate.data.storage.impl.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.common.model.Pair;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.data.model.enums.SearchCondition;
import ru.yandex.practicum.filmorate.data.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String GET_FILM_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String ADD_FILM_QUERY = "INSERT INTO film (name, description, release_date, mpa_id, duration_minutes) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?, mpa_id = ?, duration_minutes = ? WHERE id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT * FROM film";

    private static final String FILMS_SEARCH_QUERY = """
            SELECT DISTINCT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.mpa_id,
                   f.duration_minutes,
                   COALESCE(l.likes, 0) AS likes
            FROM film f
            LEFT JOIN film_director fd ON f.id = fd.film_id
            LEFT JOIN director d ON fd.director_id = d.id
            LEFT JOIN (
                SELECT film_id, COUNT(user_id) AS likes
                FROM film_like
                GROUP BY film_id
            ) l ON f.id = l.film_id
            WHERE %s
            ORDER BY likes DESC
            """;

    private static final String GET_POPULAR_FILMS_QUERY = """
            SELECT ff.id,
                   ff.name,
                   ff.description,
                   ff.release_date,
                   ff.mpa_id,
                   ff.duration_minutes,
                   COALESCE(l.likes, 0) AS likes
            FROM (
                SELECT f.id,
                       f.name,
                       f.description,
                       f.release_date,
                       f.mpa_id,
                       f.duration_minutes
                FROM film AS f
                WHERE (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
                  AND (
                      ? IS NULL OR EXISTS (
                          SELECT 1
                          FROM film_genre fg
                          WHERE fg.film_id = f.id
                            AND fg.genre_id = ?
                      )
                  )
            ) AS ff
            LEFT JOIN (
                SELECT film_id, COUNT(user_id) AS likes
                FROM film_like
                GROUP BY film_id
            ) AS l ON ff.id = l.film_id
            ORDER BY likes DESC
            LIMIT ?
            """;

    private static final String GET_LIKES_FOR_FILMS_QUERY = """
            SELECT film_id, user_id
            FROM film_like
            WHERE film_id IN (%s)
            """;

    private static final String GET_COMMON_FILMS_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.mpa_id,
                   f.duration_minutes,
                   COUNT(DISTINCT fl_all.user_id) AS likes_count
            FROM film f
            JOIN film_like fl1 ON f.id = fl1.film_id
            JOIN film_like fl2 ON f.id = fl2.film_id
            LEFT JOIN film_like fl_all ON f.id = fl_all.film_id
            WHERE fl1.user_id = ?
              AND fl2.user_id = ?
            GROUP BY f.id, f.name, f.description, f.release_date, f.mpa_id, f.duration_minutes
            ORDER BY likes_count DESC, f.id
            """;

    private static final String GET_RECOMMENDATIONS_QUERY = """
            WITH similar_users AS (
                SELECT fl2.user_id AS user_id, COUNT(*) AS common_likes
                FROM film_like fl1
                JOIN film_like fl2 ON fl1.film_id = fl2.film_id
                WHERE fl1.user_id = ? AND fl2.user_id <> ?
                GROUP BY fl2.user_id
            ),
            most_similar_users AS (
                SELECT user_id
                FROM similar_users
                WHERE common_likes = (SELECT MAX(common_likes) FROM similar_users)
            )
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.mpa_id,
                   f.duration_minutes,
                   COUNT(DISTINCT fl_all.user_id) AS likes_count
            FROM film f
            JOIN film_like fl ON f.id = fl.film_id
            JOIN most_similar_users msu ON msu.user_id = fl.user_id
            LEFT JOIN film_like fl_all ON fl_all.film_id = f.id
            WHERE NOT EXISTS (
                SELECT 1
                FROM film_like own_likes
                WHERE own_likes.film_id = f.id
                  AND own_likes.user_id = ?
            )
            GROUP BY f.id, f.name, f.description, f.release_date, f.mpa_id, f.duration_minutes
            ORDER BY likes_count DESC, f.id
            """;

    private static final String DELETE_FILM_QUERY = "DELETE FROM film WHERE id=?";

    private static final String GET_DIRECTOR_FILMS_QUERY = """
            SELECT f.id AS id, f.name AS name, f.description AS description, m.name AS mpa, f.duration_minutes AS duration_minutes
            FROM film_director AS fd
            JOIN film AS f ON f.id = fd.film_id
            JOIN mpa AS m ON f.mpa_id = m.id
            WHERE fd.director_id = ?
            """;

    private static final String ID_COLUMN_LABEL = "id";
    private static final String FILM_ID_COLUMN_LABEL = "film_id";
    private static final String USER_ID_COLUMN_LABEL = "user_id";

    private static final String ADD_LIKE_QUERY = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";

    private static final String GET_FILMS_LOG = "Searching for films";
    private static final String GET_FILM_FAILED_LOG = "Failed to find film with id = {}";

    private final JdbcTemplate jdbc;

    private static final FilmRowMapper mapper = new FilmRowMapper();

    @Override
    public boolean hasFilmId(Long id) {
        if (id == null)
            return false;
        return !jdbc.queryForList(GET_FILM_QUERY, id).isEmpty();
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        int rowsAffected = jdbc.update(ADD_LIKE_QUERY, filmId, userId);
        return rowsAffected > 0;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        int rowsAffected = jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
        return rowsAffected > 0;
    }

    @Override
    public Map<Long, Set<Long>> getFilmsLikes(List<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        List<Pair<Long, Long>> entries = jdbc.query(
                String.format(GET_LIKES_FOR_FILMS_QUERY, inSql),
                filmIds.toArray(),
                (rs, rowNum) -> new Pair<>(rs.getLong(FILM_ID_COLUMN_LABEL), rs.getLong(USER_ID_COLUMN_LABEL)));

        Map<Long, Set<Long>> result = new HashMap<>();
        entries.forEach(entry -> {
            if (!result.containsKey(entry.first)) {
                result.put(entry.first, new HashSet<>());
            }
            result.get(entry.first).add(entry.second);
        });
        return result;
    }

    @Override
    @Transactional
    public Film addFilm(Film newFilm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_FILM_QUERY, new String[] { ID_COLUMN_LABEL });
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            ps.setLong(4, newFilm.getMpaId());
            ps.setLong(5, newFilm.getDurationMinutes());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() == null)
            return null;
        long filmId = keyHolder.getKey().longValue();
        newFilm.setId(filmId);
        return newFilm;
    }

    @Override
    @Transactional
    public Optional<Film> getFilm(Long id) {
        try {
            Film film = jdbc.queryForObject(GET_FILM_QUERY, mapper, id);
            if (film == null)
                return Optional.empty();
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            log.info(GET_FILM_FAILED_LOG, id);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Film updateFilm(Film updatedFilm) {
        Long filmId = updatedFilm.getId();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE_FILM_QUERY);
            ps.setString(1, updatedFilm.getName());
            ps.setString(2, updatedFilm.getDescription());
            ps.setDate(3, Date.valueOf(updatedFilm.getReleaseDate()));
            ps.setLong(4, updatedFilm.getMpaId());
            ps.setLong(5, updatedFilm.getDurationMinutes());
            ps.setLong(6, filmId);
            return ps;
        });
        return updatedFilm;
    }

    @Override
    @Transactional
    public Film removeFilm(Long id) {
        Film removed = getFilm(id).orElse(null);
        if (removed != null) {
            jdbc.update(DELETE_FILM_QUERY, id);
        }
        return removed;
    }

    @Override
    @Transactional
    public List<Film> getAllFilms() {
        log.trace(GET_FILMS_LOG);
        return jdbc.query(GET_ALL_FILMS_QUERY, mapper);
    }

    @Override
    public List<Film> getPopularFilms(Integer limit, Long genreId, Integer year) {
        return jdbc.query(
                GET_POPULAR_FILMS_QUERY,
                mapper,
                year, year,
                genreId, genreId,
                limit);
    }

    @Override
    public List<Film> getDirectorFilms(Long id, String sortBy) {
        return null;
    }

    @Override
    public List<Film> searchFilms(String query, Set<SearchCondition> by) {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        String likeQuery = "%" + query.toLowerCase() + "%";

        if (by.contains(SearchCondition.TITLE)) {
            conditions.add("LOWER(f.name) LIKE ?");
            params.add(likeQuery);
        }

        if (by.contains(SearchCondition.DIRECTOR)) {
            conditions.add("LOWER(d.name) LIKE ?");
            params.add(likeQuery);
        }

        if (conditions.isEmpty()) {
            return Collections.emptyList();
        }

        String whereClause = String.join(" OR ", conditions);

        String sql = String.format(FILMS_SEARCH_QUERY, whereClause);

        return jdbc.query(sql, mapper, params.toArray());
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return jdbc.query(GET_COMMON_FILMS_QUERY, mapper, userId, friendId);
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        return jdbc.query(GET_RECOMMENDATIONS_QUERY, mapper, userId, userId, userId);
    }
}