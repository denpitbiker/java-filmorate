package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;

@Slf4j
@Repository
public class UserRepository {
    private static final String ADDING_USER_TRACE_MSG = "Adding new user: {}";
    private static final String DUPLICATE_USER_FOUND_TRACE_MSG = "Already have user with id: {}";
    private static final String ADDED_USER_TRACE_MSG = "New user added: {}";
    private static final String UPDATING_USER_TRACE_MSG = "Updating user: {}";
    private static final String USER_NOT_FOUND_TRACE_MSG = "Can't find user with id: {}";
    private static final String UPDATED_USER_TRACE_MSG = "User updated: {}";

    private static final String USER_NOT_FOUND_ERR_MSG = "Can't find user with id = ";
    private static final String DUPLICATE_USER_ERR_MSG = "User already exists with id = ";

    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    public User addUser(User newUser) {
        log.trace(ADDING_USER_TRACE_MSG, newUser);
        Long newUserId = newUser.getId();
        if (users.containsKey(newUserId)) {
            log.trace(DUPLICATE_USER_FOUND_TRACE_MSG, newUser);
            throw new DuplicatedDataException(DUPLICATE_USER_ERR_MSG + newUserId);
        }
        if (newUserId == null) newUserId = ++idCounter;
        else idCounter = max(idCounter, newUserId);
        setLoginAsNameOnNull(newUser);
        newUser.setId(newUserId);
        users.put(newUserId, newUser);
        User savedUser = users.get(newUserId);
        log.trace(ADDED_USER_TRACE_MSG, savedUser);
        return savedUser;
    }

    public User updateUser(User updatedUser) {
        log.trace(UPDATING_USER_TRACE_MSG, updatedUser);
        Long updatedUserId = updatedUser.getId();
        if (updatedUserId == null || !users.containsKey(updatedUserId)) {
            log.trace(USER_NOT_FOUND_TRACE_MSG, updatedUser);
            throw new NotFoundException(USER_NOT_FOUND_ERR_MSG + updatedUserId);
        }
        setLoginAsNameOnNull(updatedUser);
        users.put(updatedUserId, updatedUser);
        User savedUser = users.get(updatedUserId);
        log.trace(UPDATED_USER_TRACE_MSG, savedUser);
        return savedUser;
    }

    private void setLoginAsNameOnNull(User user) {
        if (user.getName() == null) user.setName(user.getLogin());
    }

    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }
}
