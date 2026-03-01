package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User newUser);

    User getUser(Long id);

    User updateUser(User updatedUser);

    User removeUser(Long id);

    List<User> getAllUsers();
}
