package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

import java.util.HashSet;

public class FilmDtoToFilmMapper implements Mapper<FilmDto, Film> {

    @Override
    public Film map(FilmDto value) {
        return new Film(
                value.getId(),
                value.getName(),
                value.getDescription(),
                value.getReleaseDate(),
                value.getMpa().getId(),
                value.getDurationMinutes(),
                new HashSet<>(value.getLikesIds()),
                value.getGenres() == null ? null : new HashSet<>(value.getGenres().stream()
                        .map(GenreDto::getId)
                        .toList()
                )
        );
    }
}
