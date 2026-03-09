package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Mpa;
import ru.yandex.practicum.filmorate.presentation.dto.MpaDto;

public class MpaToMpaDtoMapper implements Mapper<Mpa, MpaDto> {

    @Override
    public MpaDto map(Mpa value) {
        return new MpaDto(value.id(), value.name());
    }
}
