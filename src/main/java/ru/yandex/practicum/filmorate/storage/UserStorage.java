package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    boolean hasUserId(Long id);

    User addUser(User newUser);

    Optional<User> getUser(Long id);

    User updateUser(User updatedUser);

    User removeUser(Long id);

    List<User> getAllUsers();
}
