package ru.yandex.practicum.filmorate.domain.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.data.model.Film;
import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.data.model.Mpa;
import ru.yandex.practicum.filmorate.data.storage.api.GenreStorage;
import ru.yandex.practicum.filmorate.data.storage.api.MpaStorage;
import ru.yandex.practicum.filmorate.presentation.dto.FilmDto;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmToFilmDtoMapper implements Mapper<Film, FilmDto> {

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private static final MpaToMpaDtoMapper mpaMapper = new MpaToMpaDtoMapper();
    private static final GenreToGenreDtoMapper genreMapper = new GenreToGenreDtoMapper();

    @Override
    public FilmDto map(Film value) {
        Optional<Mpa> mpa = mpaStorage.getMpa(value.getMpaId());
        if (mpa.isEmpty()) return null;

        FilmDto filmDto = new FilmDto(
                value.getId(),
                value.getName(),
                value.getDescription(),
                value.getReleaseDate(),
                mpaMapper.map(mpa.get()),
                value.getDurationMinutes(),
                value.getGenresIds() == null ? null : new LinkedHashSet<>(value.getGenresIds().stream()
                        .map((id) -> {
                            Optional<Genre> genre = genreStorage.getGenre(id);
                            return genre.map(genreMapper::map).orElse(null);
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(GenreDto::getId))
                        .toList()
                )
        );
        filmDto.getLikesIds().addAll(value.getLikesIds());
        return filmDto;
    }
}
