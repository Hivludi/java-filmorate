package ru.yandex.practicum.filmorate.reviewlikes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReviewLike {
    private int reviewId;
    private int userId;
    private boolean isPositive;
}
