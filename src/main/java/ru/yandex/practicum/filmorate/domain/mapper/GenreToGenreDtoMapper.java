package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

public class GenreToGenreDtoMapper implements Mapper<Genre, GenreDto> {

    @Override
    public GenreDto map(Genre value) {
        return new GenreDto(value.id(), value.name());
    }
}
