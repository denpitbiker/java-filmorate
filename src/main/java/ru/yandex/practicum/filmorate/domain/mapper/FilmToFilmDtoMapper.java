package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.domain.model.FilmAdditionalInfo;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;

import java.util.*;

public class FilmToFilmDtoMapper {
    private static final MpaToMpaDtoMapper mpaMapper = new MpaToMpaDtoMapper();
    private static final GenreToGenreDtoMapper genreMapper = new GenreToGenreDtoMapper();

    public Film toData(FilmDto value) {
        return new Film(
                value.getId(),
                value.getName(),
                value.getDescription(),
                value.getReleaseDate(),
                value.getMpa().getId(),
                value.getDurationMinutes()
        );
    }

    public FilmDto toPresentation(Film value, FilmAdditionalInfo info) {
        FilmDto filmDto = new FilmDto(
                value.getId(),
                value.getName(),
                value.getDescription(),
                value.getReleaseDate(),
                mpaMapper.toPresentation(info.mpa()),
                value.getDurationMinutes(),
                info.genres() != null ?
                        new LinkedHashSet<>(info.genres().stream().map(genreMapper::toPresentation).toList()) : null
        );
        if (info.likesIds() != null) filmDto.getLikesIds().addAll(info.likesIds());
        return filmDto;
    }
}
