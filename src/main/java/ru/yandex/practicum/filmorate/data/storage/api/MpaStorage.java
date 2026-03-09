package ru.yandex.practicum.filmorate.data.storage.api;

import ru.yandex.practicum.filmorate.data.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    public Collection<Mpa> getAllMpas();

    public Optional<Mpa> getMpa(Long id);
}