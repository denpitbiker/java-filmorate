package ru.yandex.practicum.filmorate.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.annotation.DbStorage;
import ru.yandex.practicum.filmorate.data.model.Director;
import ru.yandex.practicum.filmorate.data.storage.api.DirectorStorage;
import ru.yandex.practicum.filmorate.domain.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.domain.mapper.DirectorToDirectorDtoMapper;
import ru.yandex.practicum.filmorate.presentation.dto.DirectorDto;

import java.util.Collection;

@Service
@Slf4j
public class DirectorService {
    private static final String GET_ALL_DIRECTORS_LOG_MSG = "Get all directors started";
    private static final String GET_DIRECTOR_LOG_MSG = "Get director with id: {}";

    private static final String DUPLICATE_DIRECTOR_FOUND_TRACE_MSG = "Direct already exists with id: {}";
    private static final String DIRECTOR_NOT_FOUND_TRACE_MSG = "Can't find director with id: {}";

    private static final String DUPLICATE_DIRECTOR_FOUND_EXCEPTION_MSG = "Direct already exists with id = ";
    private static final String DIRECTOR_NOT_FOUND_EXCEPTION_MSG = "Can't find director with id = ";


    private final DirectorStorage directorStorage;

    private static final DirectorToDirectorDtoMapper directorMapper = new DirectorToDirectorDtoMapper();

    public DirectorService(@DbStorage DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Collection<DirectorDto> getAllDirectors() {
        return directorStorage
                .getAllDirectors()
                .stream()
                .map(directorMapper::toPresentation)
                .toList();
    }

    public DirectorDto getDirector(Long id) {
        return directorMapper.toPresentation(getDirectorOrThrow(id));
    }

    public DirectorDto addDirector(DirectorDto newDirector) {
        checkDirectorIdNotExist(newDirector.getId());
        Director director = directorStorage.addDirector(directorMapper.toData(newDirector));
        return directorMapper.toPresentation(director);
    }

    public DirectorDto updateDirector(DirectorDto updatedDirector) {
        checkDirectorIdExists(updatedDirector.getId());
        Director director = directorStorage.updateDirector(directorMapper.toData(updatedDirector));
        return directorMapper.toPresentation(director);
    }

    public DirectorDto deleteDirector(Long id) {
        Director removedDirector = directorStorage.deleteDirector(id);
        if (removedDirector == null) {
            throw new NotFoundException(DIRECTOR_NOT_FOUND_TRACE_MSG + id);
        }
        return directorMapper.toPresentation(removedDirector);
    }

    private Director getDirectorOrThrow(Long id) {
        return directorStorage
                .getDirector(id)
                .orElseThrow(() -> new NotFoundException(DIRECTOR_NOT_FOUND_TRACE_MSG + id));
    }

    private void checkDirectorIdNotExist(Long id) {
        if (directorStorage.hasDirectorId(id)) {
            log.trace(DUPLICATE_DIRECTOR_FOUND_TRACE_MSG, id);
            throw new DuplicatedDataException(DUPLICATE_DIRECTOR_FOUND_EXCEPTION_MSG + id);
        }
    }

    private void checkDirectorIdExists(Long id) {
        if (!directorStorage.hasDirectorId(id)) {
            log.trace(DIRECTOR_NOT_FOUND_TRACE_MSG, id);
            throw new NotFoundException(DIRECTOR_NOT_FOUND_EXCEPTION_MSG + id);
        }
    }
}
