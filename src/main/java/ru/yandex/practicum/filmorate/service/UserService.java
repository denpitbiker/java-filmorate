package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final String GET_USER_LOG_MSG = "Get user {}";
    private static final String ADD_FRIEND_LOG_MSG = "Add friend {} to user {}";
    private static final String REMOVE_FRIEND_LOG_MSG = "Remove friend {} from user {}";
    private static final String GET_USER_FRIENDS_LOG_MSG = "Get friends for user {}";
    private static final String GET_COMMON_FRIENDS_LOG_MSG = "Get common friends for users {} and {}";
    private static final String GET_USERS_LOG_MSG = "Get all users";
    private static final String ADD_USER_LOG_MSG = "Add new user {}";
    private static final String UPDATE_USER_LOG_MSG = "Update user {}";

    private static final String USER_IDS_ARE_EQUAL_ERR_MSG = "User ids should not be the same!";

    private final UserStorage userStorage;

    public void addFriend(Long id, Long friendId) {
        log.info(ADD_FRIEND_LOG_MSG, friendId, id);
        checkIdsAreNotTheSame(id, friendId);
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(Long id, Long friendId) {
        log.info(REMOVE_FRIEND_LOG_MSG, friendId, id);
        checkIdsAreNotTheSame(id, friendId);
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public Collection<User> getUserFriends(Long id) {
        log.info(GET_USER_FRIENDS_LOG_MSG, id);
        Set<Long> userFriendsIds = userStorage.getUser(id).getFriends();
        return userStorage.getAllUsers().stream().filter((user -> userFriendsIds.contains(user.getId()))).toList();
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info(GET_COMMON_FRIENDS_LOG_MSG, id, otherId);
        checkIdsAreNotTheSame(id, otherId);
        Set<Long> userFriendsIds = userStorage.getUser(id).getFriends();
        userFriendsIds.retainAll(userStorage.getUser(otherId).getFriends());
        return userStorage.getAllUsers().stream().filter((user -> userFriendsIds.contains(user.getId()))).toList();
    }

    public User getUser(Long id) {
        log.info(GET_USER_LOG_MSG, id);
        return userStorage.getUser(id);
    }

    public Collection<User> getAllUsers() {
        log.info(GET_USERS_LOG_MSG);
        return userStorage.getAllUsers();
    }

    public User addUser(User newUser) {
        log.info(ADD_USER_LOG_MSG, newUser);
        return userStorage.addUser(newUser);
    }

    public User updateUser(User updatedUser) {
        log.info(UPDATE_USER_LOG_MSG, updatedUser);
        return userStorage.updateUser(updatedUser);
    }

    private void checkIdsAreNotTheSame(Long id1, Long id2) {
        if (id1.equals(id2)) throw new ValidationException(USER_IDS_ARE_EQUAL_ERR_MSG);
    }
}
