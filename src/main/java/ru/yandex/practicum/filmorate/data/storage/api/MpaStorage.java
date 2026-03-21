package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Mpa;

import java.util.*;

public interface MpaStorage {

    Collection<Mpa> getAllMpas();

    Optional<Mpa> getMpa(Long id);

    Map<Long, Mpa> getMpasInfo(Set<Long> mpaIds);
}