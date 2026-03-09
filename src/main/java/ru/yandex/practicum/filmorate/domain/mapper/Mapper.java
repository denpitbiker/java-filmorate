package ru.yandex.practicum.filmorate.domain.mapper;

public interface Mapper<F, T> {

    T map(F value);
}
