package ru.yandex.practicum.filmorate.domain.model;

import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.data.model.Mpa;

import java.util.LinkedHashSet;
import java.util.Set;

public record FilmAdditionalInfo(
        Mpa mpa,
        LinkedHashSet<Genre> genres,
        Set<Long> likesIds
) {

}
