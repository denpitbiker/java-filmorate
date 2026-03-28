package ru.yandex.practicum.filmorate.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.domain.service.UserService;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(UserController.CONTROLLER_ROUTE)
@RestController
public class UserController {
    private static final String ID_PATH_VAR = "id";
    private static final String FRIEND_ID_PATH_VAR = "userId";
    private static final String OTHER_ID_PATH_VAR = "otherId";

    public static final String CONTROLLER_ROUTE = "/users";
    public static final String GET_USER_SUBROUTE = "/{" + ID_PATH_VAR + "}";
    public static final String DELETE_USER_SUBROUTE = "/{" + ID_PATH_VAR + "}";
    public static final String ADD_FRIEND_SUBROUTE = "/{" + ID_PATH_VAR + "}/friends/{" + FRIEND_ID_PATH_VAR + "}";
    public static final String REMOVE_FRIEND_SUBROUTE = "/{" + ID_PATH_VAR + "}/friends/{" + FRIEND_ID_PATH_VAR + "}";
    public static final String GET_FRIENDS_SUBROUTE = "/{" + ID_PATH_VAR + "}/friends";
    public static final String GET_COMMON_FRIENDS_SUBROUTE =  "/{" + ID_PATH_VAR + "}/friends/common/{" + OTHER_ID_PATH_VAR + "}";

    private static final String GET_USER_LOG_MSG = "Get user {} request";
    private static final String DELETE_USER_LOG_MSG = "Delete user {} request";
    private static final String ADD_FRIEND_LOG_MSG = "Add friend {} to user {} request";
    private static final String REMOVE_FRIEND_LOG_MSG = "Remove friend {} from user {} request";
    private static final String GET_USER_FRIENDS_LOG_MSG = "Get friends for user {} request";
    private static final String GET_COMMON_FRIENDS_LOG_MSG = "Get common friends for users {} and {} request";
    private static final String GET_USERS_LOG_MSG = "Get all users request";
    private static final String ADD_USER_LOG_MSG = "Add new user request {}";
    private static final String UPDATE_USER_LOG_MSG = "Update user request {}";

    private final UserService userService;

    @PutMapping(ADD_FRIEND_SUBROUTE)
    public void addFriend(@PathVariable(ID_PATH_VAR) Long id, @PathVariable(FRIEND_ID_PATH_VAR) Long friendId) {
        log.info(ADD_FRIEND_LOG_MSG, friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(REMOVE_FRIEND_SUBROUTE)
    public void removeFriend(@PathVariable(ID_PATH_VAR) Long id, @PathVariable(FRIEND_ID_PATH_VAR) Long friendId) {
        log.info(REMOVE_FRIEND_LOG_MSG, friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping(GET_FRIENDS_SUBROUTE)
    public Collection<UserDto> getUserFriends(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(GET_USER_FRIENDS_LOG_MSG, id);
        return userService.getUserFriends(id);
    }

    @GetMapping(GET_COMMON_FRIENDS_SUBROUTE)
    public Collection<UserDto> getCommonFriends(@PathVariable(ID_PATH_VAR) Long id, @PathVariable(OTHER_ID_PATH_VAR) Long otherId) {
        log.info(GET_COMMON_FRIENDS_LOG_MSG, id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping(GET_USER_SUBROUTE)
    public UserDto getUser(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(GET_USER_LOG_MSG, id);
        return userService.getUser(id);
    }

    @DeleteMapping(DELETE_USER_SUBROUTE)
    public UserDto deleteUser(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(DELETE_USER_LOG_MSG, id);
        return userService.deleteUser(id);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info(GET_USERS_LOG_MSG);
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto newUser) {
        log.info(ADD_USER_LOG_MSG, newUser);
        return userService.addUser(newUser);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto updatedUser) {
        log.info(UPDATE_USER_LOG_MSG, updatedUser);
        return userService.updateUser(updatedUser);
    }
}
