package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.User;
import ru.yandex.practicum.filmorate.presentation.dto.UserDto;

public class UserToUserDtoMapper implements Mapper<User, UserDto> {

    @Override
    public UserDto map(User value) {
        UserDto userDto = new UserDto(
                value.getId(),
                value.getEmail(),
                value.getLogin(),
                value.getName() != null ? value.getName() : value.getLogin(),
                value.getBirthday()
        );
        userDto.getFriends().addAll(value.getFriends());
        return userDto;
    }
}
