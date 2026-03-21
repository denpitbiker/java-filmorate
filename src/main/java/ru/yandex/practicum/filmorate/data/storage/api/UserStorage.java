package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    boolean hasUserId(Long id);

    boolean addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    User addUser(User newUser);

    Optional<User> getUser(Long id);

    User updateUser(User updatedUser);

    User removeUser(Long id);

    List<User> getAllUsers();

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherId);
}
