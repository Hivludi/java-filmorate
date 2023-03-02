package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    List<Review> getAllReviews();

    Optional<Review> create(Review review);

    Optional<Review> findReviewById(int id);

    Optional<Review> addLike(ReviewLike reviewLike);

    Optional<Review> addDislike(ReviewLike reviewLike);

    Optional<Review> removeLike(ReviewLike reviewLike);

    Optional<Review> update(Review review);

    Optional<Review> delete(int reviewId);

    List<Review> getReviewsByFilmId(int filmId, int count);
}