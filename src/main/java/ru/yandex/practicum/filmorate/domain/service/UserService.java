package ru.yandex.practicum.filmorate.domain.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.data.storage.api.UserStorage;
import ru.yandex.practicum.filmorate.domain.mapper.UserToUserDtoMapper;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private static final String GET_USER_LOG_MSG = "Get user {}";
    private static final String DELETE_USER_LOG_MSG = "Delete user {}";
    private static final String ADD_FRIEND_LOG_MSG = "Add friend {} to user {}";
    private static final String REMOVE_FRIEND_LOG_MSG = "Remove friend {} from user {}";
    private static final String ADDED_FRIEND_LOG_MSG = "Is success add friend {} to user {}: {}";
    private static final String REMOVED_FRIEND_LOG_MSG = "Is success remove friend {} from user {}: {}";
    private static final String GET_USER_FRIENDS_LOG_MSG = "Get friends for user {}";
    private static final String GET_COMMON_FRIENDS_LOG_MSG = "Get common friends for users {} and {}";
    private static final String GET_USERS_LOG_MSG = "Get all users";
    private static final String ADD_USER_LOG_MSG = "Add new user {}";
    private static final String UPDATE_USER_LOG_MSG = "Update user {}";
    private static final String USER_NOT_FOUND_TRACE_MSG = "Can't find user with id: {}";
    private static final String DUPLICATE_USER_FOUND_TRACE_MSG = "Already have user with id: {}";

    private static final String USER_IDS_ARE_EQUAL_ERR_MSG = "User ids should not be the same!";
    private static final String USER_NOT_FOUND_ERR_MSG = "Can't find user with id = ";
    private static final String DUPLICATE_USER_ERR_MSG = "User already exists with id = ";

    private final UserStorage userStorage;
    private static final UserToUserDtoMapper userDtoToUserMapper = new UserToUserDtoMapper();

    public UserService(@DbStorage UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long id, Long friendId) {
        log.info(ADD_FRIEND_LOG_MSG, friendId, id);
        checkIdsAreNotTheSame(id, friendId);
        getUserOrThrow(id);
        getUserOrThrow(friendId);
        boolean isSuccess = userStorage.addFriend(id, friendId);
        log.info(ADDED_FRIEND_LOG_MSG, friendId, id, isSuccess);
    }

    public void removeFriend(Long id, Long friendId) {
        log.info(REMOVE_FRIEND_LOG_MSG, friendId, id);
        checkIdsAreNotTheSame(id, friendId);
        getUserOrThrow(id);
        getUserOrThrow(friendId);
        boolean isSuccess = userStorage.removeFriend(id, friendId);
        log.info(REMOVED_FRIEND_LOG_MSG, friendId, id, isSuccess);
    }

    public Collection<UserDto> getUserFriends(Long id) {
        log.info(GET_USER_FRIENDS_LOG_MSG, id);
        getUserOrThrow(id);
        List<User> userFriends = userStorage.getFriends(id);
        return userFriends.stream()
                .map(userDtoToUserMapper::toPresentation)
                .toList();
    }

    public Collection<UserDto> getCommonFriends(Long id, Long otherId) {
        log.info(GET_COMMON_FRIENDS_LOG_MSG, id, otherId);
        checkIdsAreNotTheSame(id, otherId);
        getUserOrThrow(id);
        getUserOrThrow(otherId);
        List<User> userFriends = userStorage.getCommonFriends(id, otherId);
        return userFriends.stream()
                .map(userDtoToUserMapper::toPresentation)
                .toList();
    }

    public UserDto getUser(Long id) {
        log.info(GET_USER_LOG_MSG, id);
        return getUserDtoOrThrow(id);
    }

    public UserDto deleteUser(Long id) {
        log.info(DELETE_USER_LOG_MSG, id);
        User removed = userStorage.removeUser(id);
        if (removed == null) throw new NotFoundException(USER_NOT_FOUND_ERR_MSG + id);
        return userDtoToUserMapper.toPresentation(removed);
    }

    public Collection<UserDto> getAllUsers() {
        log.info(GET_USERS_LOG_MSG);
        return userStorage.getAllUsers().stream()
                .map(userDtoToUserMapper::toPresentation)
                .toList();
    }

    public UserDto addUser(UserDto newUser) {
        log.info(ADD_USER_LOG_MSG, newUser);
        checkUserIdNotExist(newUser.getId());
        return userDtoToUserMapper.toPresentation(userStorage.addUser(userDtoToUserMapper.toData(newUser)));
    }

    public UserDto updateUser(UserDto updatedUser) {
        log.info(UPDATE_USER_LOG_MSG, updatedUser);
        checkUserIdExist(updatedUser.getId());
        return userDtoToUserMapper.toPresentation(userStorage.updateUser(userDtoToUserMapper.toData(updatedUser)));
    }

    private User getUserOrThrow(Long id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_ERR_MSG + id));
    }

    private UserDto getUserDtoOrThrow(Long id) {
        return userDtoToUserMapper.toPresentation(getUserOrThrow(id));
    }

    private void checkUserIdExist(Long id) {
        if (!userStorage.hasUserId(id)) {
            log.trace(USER_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(USER_NOT_FOUND_ERR_MSG + id);
        }
    }

    private void checkUserIdNotExist(Long id) {
        if (userStorage.hasUserId(id)) {
            log.trace(DUPLICATE_USER_FOUND_TRACE_MSG, id);
            throw new DuplicatedDataException(DUPLICATE_USER_ERR_MSG + id);
        }
    }

    private void checkIdsAreNotTheSame(Long id1, Long id2) {
        if (id1.equals(id2)) throw new ValidationException(USER_IDS_ARE_EQUAL_ERR_MSG);
    }
}
