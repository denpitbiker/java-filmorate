package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    boolean hasUserId(Long id);

    Optional<User> addUser(User newUser);

    Optional<User> getUser(Long id);

    Optional<User> updateUser(User updatedUser);

    Optional<User> removeUser(Long id);

    List<User> getAllUsers();
}
