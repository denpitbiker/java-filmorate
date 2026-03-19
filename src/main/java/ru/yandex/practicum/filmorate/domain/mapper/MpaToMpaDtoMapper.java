package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Mpa;
import ru.yandex.practicum.filmorate.presentation.dto.MpaDto;

public class MpaToMpaDtoMapper implements TwoWayDataMapper<Mpa, MpaDto> {

    @Override
    public Mpa toData(MpaDto value) {
        return new Mpa(value.getId(), value.getName());
    }

    @Override
    public MpaDto toPresentation(Mpa value) {
        return new MpaDto(value.id(), value.name());
    }
}
