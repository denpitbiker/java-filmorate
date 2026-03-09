package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

import java.util.HashSet;

public class UserDtoToUserMapper implements Mapper<UserDto, User> {

    @Override
    public User map(UserDto value) {
        return new User(
                value.getId(),
                value.getEmail(),
                value.getLogin(),
                value.getName(),
                value.getBirthday(),
                new HashSet<>(value.getFriends())
        );
    }
}
