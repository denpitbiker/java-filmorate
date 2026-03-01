package ru.yandex.practicum.filmorate.storage;

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
public class InMemoryUserStorage implements UserStorage {
    private static final String ADDING_USER_TRACE_MSG = "Adding new user: {}";
    private static final String GET_USER_TRACE_MSG = "Get user: {}";
    private static final String GET_ALL_USERS_TRACE_MSG = "Get all users";
    private static final String REMOVE_USER_TRACE_MSG = "Remove user: {}";
    private static final String DUPLICATE_USER_FOUND_TRACE_MSG = "Already have user with id: {}";
    private static final String ADDED_USER_TRACE_MSG = "New user added: {}";
    private static final String GOT_USER_TRACE_MSG = "Get user: {}";
    private static final String REMOVED_USER_TRACE_MSG = "Removed user: {}";
    private static final String UPDATING_USER_TRACE_MSG = "Updating user: {}";
    private static final String USER_NOT_FOUND_TRACE_MSG = "Can't find user with id: {}";
    private static final String UPDATED_USER_TRACE_MSG = "User updated: {}";

    private static final String USER_NOT_FOUND_ERR_MSG = "Can't find user with id = ";
    private static final String DUPLICATE_USER_ERR_MSG = "User already exists with id = ";

    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
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
        users.put(newUserId, newUser.clone());
        log.trace(ADDED_USER_TRACE_MSG, newUser);
        return newUser;
    }

    @Override
    public User getUser(Long id) {
        log.trace(GET_USER_TRACE_MSG, id);
        checkUserIdExists(id);
        User user = users.get(id).clone();
        log.trace(GOT_USER_TRACE_MSG, user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        log.trace(UPDATING_USER_TRACE_MSG, updatedUser);
        Long updatedUserId = updatedUser.getId();
        checkUserIdExists(updatedUserId);
        setLoginAsNameOnNull(updatedUser);
        users.put(updatedUserId, updatedUser.clone());
        log.trace(UPDATED_USER_TRACE_MSG, updatedUser);
        return updatedUser;
    }

    @Override
    public User deleteUser(Long id) {
        log.trace(REMOVE_USER_TRACE_MSG, id);
        checkUserIdExists(id);
        User removed = users.remove(id);
        log.trace(REMOVED_USER_TRACE_MSG, removed);
        return removed;
    }

    @Override
    public List<User> getAllUsers() {
        log.trace(GET_ALL_USERS_TRACE_MSG);
        return users.values().stream().map(User::clone).toList();
    }

    private void setLoginAsNameOnNull(User user) {
        if (user.getName() == null) user.setName(user.getLogin());
    }

    private void checkUserIdExists(Long id) {
        if (id == null || !users.containsKey(id)) {
            log.trace(USER_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(USER_NOT_FOUND_ERR_MSG + id);
        }
    }
}
