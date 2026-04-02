package ru.yandex.practicum.filmorate.data.model.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.data.model.enums.SortBy;

@Component
public class StringToSortByConverter implements Converter<String, SortBy> {
    @Override
    public SortBy convert(String source) {
        return SortBy.valueOf(source.toUpperCase());
    }
}
