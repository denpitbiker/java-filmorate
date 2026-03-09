package ru.yandex.practicum.filmorate.data.storage.impl.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.data.annotation.InMemoryStorage;
import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.max;

@Slf4j
@Repository
@InMemoryStorage
public class InMemoryUserStorage implements UserStorage {
    private static final String ADDING_USER_TRACE_MSG = "Adding new user: {}";
    private static final String GET_USER_TRACE_MSG = "Get user: {}";
    private static final String GET_ALL_USERS_TRACE_MSG = "Get all users";
    private static final String REMOVE_USER_TRACE_MSG = "Remove user: {}";
    private static final String ADDED_USER_TRACE_MSG = "New user added: {}";
    private static final String GOT_USER_TRACE_MSG = "Get user: {}";
    private static final String REMOVED_USER_TRACE_MSG = "Removed user: {}";
    private static final String UPDATING_USER_TRACE_MSG = "Updating user: {}";
    private static final String UPDATED_USER_TRACE_MSG = "User updated: {}";

    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public User addUser(User newUser) {
        log.trace(ADDING_USER_TRACE_MSG, newUser);
        Long newUserId = newUser.getId();
        if (newUserId == null) newUserId = ++idCounter;
        else idCounter = max(idCounter, newUserId);
        newUser.setId(newUserId);
        users.put(newUserId, newUser.clone());
        log.trace(ADDED_USER_TRACE_MSG, newUser);
        return newUser;
    }

    @Override
    public Optional<User> getUser(Long id) {
        log.trace(GET_USER_TRACE_MSG, id);
        User user = users.get(id);
        if (user == null) return Optional.empty();
        log.trace(GOT_USER_TRACE_MSG, user);
        return Optional.of(user.clone());
    }

    @Override
    public User updateUser(User updatedUser) {
        log.trace(UPDATING_USER_TRACE_MSG, updatedUser);
        Long updatedUserId = updatedUser.getId();
        users.put(updatedUserId, updatedUser.clone());
        log.trace(UPDATED_USER_TRACE_MSG, updatedUser);
        return updatedUser;
    }

    @Override
    public User removeUser(Long id) {
        log.trace(REMOVE_USER_TRACE_MSG, id);
        User removed = users.remove(id);
        log.trace(REMOVED_USER_TRACE_MSG, removed);
        return removed;
    }

    @Override
    public List<User> getAllUsers() {
        log.trace(GET_ALL_USERS_TRACE_MSG);
        return users.values().stream()
                .map(User::clone)
                .toList();
    }

    @Override
    public boolean hasUserId(Long id) {
        return id != null && users.containsKey(id);
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        return users.get(userId).getFriends().add(friendId);
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        return users.get(userId).getFriends().remove(friendId);
    }
}
