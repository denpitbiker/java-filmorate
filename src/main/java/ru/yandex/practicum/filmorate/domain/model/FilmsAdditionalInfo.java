package ru.yandex.practicum.filmorate.domain.model;

import ru.yandex.practicum.filmorate.data.model.Genre;
import ru.yandex.practicum.filmorate.data.model.Mpa;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public record FilmsAdditionalInfo(
        Map<Long, Mpa> mpas,
        Map<Long, Set<Long>> likes,
        Map<Long, LinkedHashSet<Genre>> genres
) {

}
