package ru.yandex.practicum.filmorate.domain.mapper;

import ru.yandex.practicum.filmorate.data.model.Review;
import ru.yandex.practicum.filmorate.presentation.dto.ReviewDto;

public class ReviewToReviewDtoMapper implements TwoWayDataMapper<Review, ReviewDto> {
    @Override
    public Review toData(ReviewDto value) {
        return new Review(value.getId(), value.getContent(), value.getIsPositive(), value.getUserId(), value.getFilmId(), value.getUseful());
    }

    @Override
    public ReviewDto toPresentation(Review value) {
        return new ReviewDto(value.getId(), value.getContent(), value.getIsPositive(), value.getUserId(), value.getFilmId(), value.getUseful());
    }
}
