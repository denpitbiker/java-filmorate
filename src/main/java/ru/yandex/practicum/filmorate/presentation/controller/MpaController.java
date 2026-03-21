package ru.yandex.practicum.filmorate.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.domain.service.MpaService;
import ru.yandex.practicum.filmorate.presentation.dto.MpaDto;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(MpaController.CONTROLLER_ROUTE)
@RestController
public class MpaController {
    private static final String ID_PATH_VAR = "id";

    public static final String CONTROLLER_ROUTE = "/mpa";
    public static final String GET_MPA_SUBROUTE = "/{id}";

    private static final String GET_MPA_LOG_MSG = "Get mpa with id = {} request";
    private static final String GET_MPAS_LOG_MSG = "Get all mpas request";

    private final MpaService mpaService;

    @GetMapping(GET_MPA_SUBROUTE)
    public MpaDto getMpa(@PathVariable(ID_PATH_VAR) Long id) {
        log.info(GET_MPA_LOG_MSG, id);
        return mpaService.getMpa(id);
    }

    @GetMapping
    public Collection<MpaDto> getAllMpas() {
        log.info(GET_MPAS_LOG_MSG);
        return mpaService.getAllMpas();
    }
}
