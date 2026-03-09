package ru.yandex.practicum.filmorate.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.model.Mpa;
import ru.yandex.practicum.filmorate.data.storage.api.MpaStorage;
import ru.yandex.practicum.filmorate.domain.exception.NotFoundException;
import ru.yandex.practicum.filmorate.domain.mapper.MpaToMpaDtoMapper;
import ru.yandex.practicum.filmorate.presentation.dto.MpaDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private static final String GET_MPA_LOG_MSG = "Get mpa {}";
    private static final String GET_MPAS_LOG_MSG = "Get all mpas";

    private static final String MPA_NOT_FOUND_ERR_MSG = "Can't find mpa with id = ";

    private static final MpaToMpaDtoMapper mapper = new MpaToMpaDtoMapper();

    private final MpaStorage mpaStorage;

    public MpaDto getMpa(Long id) {
        log.info(GET_MPA_LOG_MSG, id);
        return mapper.map(getMpaOrThrow(id));
    }

    public Collection<MpaDto> getAllMpas() {
        log.info(GET_MPAS_LOG_MSG);
        return mpaStorage.getAllMpas().stream()
                .map(mapper::map)
                .toList();
    }

    private Mpa getMpaOrThrow(Long id) {
        return mpaStorage.getMpa(id)
                .orElseThrow(() -> new NotFoundException(MPA_NOT_FOUND_ERR_MSG + id));
    }
}
