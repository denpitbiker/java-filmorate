package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> getAllMpas();

    Optional<Mpa> getMpa(Long id);
}