package ru.yandex.practicum.filmorate.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.domain.service.DirectorService;
import ru.yandex.practicum.filmorate.presentation.dto.DirectorDto;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(DirectorController.CONTROLLER_ROUTE)
public class DirectorController {
    private static final String DIRECTOR_ID_VAR = "id";

    public static final String CONTROLLER_ROUTE = "/directors";
    public static final String GET_DIRECTOR_SUBROUTE = "/{" + DIRECTOR_ID_VAR + "}";

    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping(GET_DIRECTOR_SUBROUTE)
    public DirectorDto getDirector(@PathVariable(DIRECTOR_ID_VAR) Long id) {
        return directorService.getDirector(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto addDirector(@Valid @RequestBody DirectorDto director) {
        return directorService.addDirector(director);
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody DirectorDto director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping(GET_DIRECTOR_SUBROUTE)
    public DirectorDto deleteDirector(@PathVariable(DIRECTOR_ID_VAR) Long id) {
        return directorService.deleteDirector(id);
    };
}
