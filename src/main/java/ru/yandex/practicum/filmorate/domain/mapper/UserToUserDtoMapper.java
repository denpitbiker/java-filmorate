package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

public class UserToUserDtoMapper implements TwoWayDataMapper<User, UserDto> {

    @Override
    public User toData(UserDto value) {
        return new User(
                value.getId(),
                value.getEmail(),
                value.getLogin(),
                value.getName(),
                value.getBirthday()
        );
    }

    @Override
    public UserDto toPresentation(User value) {
        return new UserDto(
                value.getId(),
                value.getEmail(),
                value.getLogin(),
                value.getName() != null ? value.getName() : value.getLogin(),
                value.getBirthday()
        );
    }
}
