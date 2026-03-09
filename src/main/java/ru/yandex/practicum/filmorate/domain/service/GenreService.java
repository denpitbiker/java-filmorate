package ru.yandex.practicum.filmorate.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.data.storage.api.GenreStorage;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.domain.mapper.GenreToGenreDtoMapper;
import ru.yandex.practicum.filmorate.presentation.dto.GenreDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private static final String GET_GENRE_LOG_MSG = "Get genre {}";
    private static final String GET_GENRES_LOG_MSG = "Get all genres";

    private static final String GENRE_NOT_FOUND_ERR_MSG = "Can't find genre with id = ";

    private static final GenreToGenreDtoMapper mapper = new GenreToGenreDtoMapper();

    private final GenreStorage genreStorage;

    public GenreDto getGenre(Long id) {
        log.info(GET_GENRE_LOG_MSG, id);
        return mapper.map(getGenreOrThrow(id));
    }

    public Collection<GenreDto> getAllGenres() {
        log.info(GET_GENRES_LOG_MSG);
        return genreStorage.getAllGenres().stream()
                .map(mapper::map)
                .toList();
    }

    private Genre getGenreOrThrow(Long id) {
        return genreStorage.getGenre(id)
                .orElseThrow(() -> new NotFoundException(GENRE_NOT_FOUND_ERR_MSG + id));
    }
}

