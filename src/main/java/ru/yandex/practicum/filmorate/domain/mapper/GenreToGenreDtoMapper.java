package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

public class GenreToGenreDtoMapper implements TwoWayDataMapper<Genre, GenreDto> {

    @Override
    public Genre toData(GenreDto value) {
        return new Genre(value.getId(), value.getName());
    }

    @Override
    public GenreDto toPresentation(Genre value) {
        return new GenreDto(value.id(), value.name());
    }
}
