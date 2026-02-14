package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private static final String GET_USERS_LOG_MSG = "All users request";
    private static final String ADD_USER_LOG_MSG = "Add new user request {}";
    private static final String UPDATE_USER_LOG_MSG = "Update user request {}";

    private final UserRepository userRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllUsers() {
        log.info(GET_USERS_LOG_MSG);
        return userRepository.getAllUsers();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User newUser) {
        log.info(ADD_USER_LOG_MSG, newUser);
        return userRepository.addUser(newUser);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info(UPDATE_USER_LOG_MSG, updatedUser);
        return userRepository.updateUser(updatedUser);
    }
}
