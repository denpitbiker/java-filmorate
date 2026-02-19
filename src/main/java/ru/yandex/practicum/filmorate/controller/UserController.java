package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(UserController.CONTROLLER_ROUTE)
@RestController
public class UserController {
    public static final String CONTROLLER_ROUTE = "/users";

    private static final String GET_USERS_LOG_MSG = "All users request";
    private static final String ADD_USER_LOG_MSG = "Add new user request {}";
    private static final String UPDATE_USER_LOG_MSG = "Update user request {}";

    private final UserRepository userRepository;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info(GET_USERS_LOG_MSG);
        return userRepository.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User newUser) {
        log.info(ADD_USER_LOG_MSG, newUser);
        return userRepository.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info(UPDATE_USER_LOG_MSG, updatedUser);
        return userRepository.updateUser(updatedUser);
    }
}
