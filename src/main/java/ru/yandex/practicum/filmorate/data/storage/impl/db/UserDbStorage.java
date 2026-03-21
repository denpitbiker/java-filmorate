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
import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;
import ru.yandex.practicum.filmorate.data.storage.impl.db.rowmapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@DbStorage
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String GET_USER_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String ADD_USER_QUERY = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id=?";
    private static final String GET_FRIENDS_FOR_USER_QUERY = """
            SELECT u.id AS id, u.name AS name, u.email AS email, u.login AS login, u.birthday AS birthday
            FROM user_friend AS uf
            JOIN users AS u ON u.id = uf.friend_id AND uf.user_id = ?
            """;
    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT u.id AS id, u.name AS name, u.email AS email, u.login AS login, u.birthday AS birthday
            FROM user_friend AS uf
            JOIN user_friend AS of ON of.friend_id = uf.friend_id AND uf.user_id = ? AND of.user_id = ?
            JOIN users AS u ON u.id = uf.friend_id
            """;

    private static final String ID_COLUMN = "id";

    private static final String ADD_FRIEND_QUERY = "INSERT INTO user_friend (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";

    private static final String GET_USERS_LOG = "Searching for user";
    private static final String GET_USER_FAILED_LOG = "Failed to find user with id = {}";

    private final JdbcTemplate jdbc;

    private static final UserRowMapper mapper = new UserRowMapper();

    @Override
    public boolean hasUserId(Long id) {
        if (id == null) return false;
        return !jdbc.queryForList(GET_USER_QUERY, id).isEmpty();
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        int rowsAffected = jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
        return rowsAffected > 0;
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        int rowsAffected = jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
        return rowsAffected > 0;
    }

    @Override
    @Transactional
    public User addUser(User newUser) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(ADD_USER_QUERY, new String[]{ID_COLUMN});
            ps.setString(1, newUser.getName());
            ps.setString(2, newUser.getEmail());
            ps.setString(3, newUser.getLogin());
            ps.setDate(4, Date.valueOf(newUser.getBirthday()));
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() == null) return null;
        long userId = keyHolder.getKey().longValue();
        newUser.setId(userId);
        return newUser;
    }

    @Override
    @Transactional
    public Optional<User> getUser(Long id) {
        try {
            User user = jdbc.queryForObject(GET_USER_QUERY, mapper, id);
            if (user == null) return Optional.empty();
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            log.info(GET_USER_FAILED_LOG, id);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public User updateUser(User updatedUser) {
        Long userId = updatedUser.getId();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE_USER_QUERY);
            ps.setString(1, updatedUser.getName());
            ps.setString(2, updatedUser.getEmail());
            ps.setString(3, updatedUser.getLogin());
            ps.setDate(4, Date.valueOf(updatedUser.getBirthday()));
            ps.setLong(5, userId);
            return ps;
        });
        return updatedUser;
    }

    @Override
    @Transactional
    public User removeUser(Long id) {
        User removed = getUser(id).orElse(null);
        if (removed != null) {
            jdbc.update(DELETE_USER_QUERY, id);
        }
        return removed;
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        log.trace(GET_USERS_LOG);
        return jdbc.query(GET_ALL_USERS_QUERY, mapper).stream()
                .toList();
    }

    @Override
    public List<User> getFriends(Long userId) {
        return jdbc.query(GET_FRIENDS_FOR_USER_QUERY, mapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return jdbc.query(GET_COMMON_FRIENDS_QUERY, mapper, userId, otherId);
    }
}
