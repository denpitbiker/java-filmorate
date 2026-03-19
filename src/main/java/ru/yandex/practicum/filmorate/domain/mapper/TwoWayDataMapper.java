package ru.yandex.practicum.filmorate.domain.mapper;

/**
 * @param <D> is used for data layer models
 * @param <P> is used for presentation layer models
 */
public interface TwoWayDataMapper<D, P> {

    D toData(P value);

    P toPresentation(D value);
}
