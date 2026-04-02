package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Director;
import ru.yandex.practicum.filmorate.presentation.dto.DirectorDto;

public class DirectorToDirectorDtoMapper implements TwoWayDataMapper<Director, DirectorDto> {
    @Override
    public Director toData(DirectorDto value) {
        return new Director(value.getId(), value.getName());
    }

    @Override
    public DirectorDto toPresentation(Director value) {
        return new DirectorDto(value.getId(), value.getName());
    }
}
