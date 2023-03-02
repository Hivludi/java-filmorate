package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Optional<Review> create(Review review) {
        return reviewStorage.create(review);
    }

    public Optional<Review> findReviewById(int reviewId) {
        return reviewStorage.findReviewById(reviewId);
    }

    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public Optional<Review> addLike(ReviewLike reviewLike) {
        return reviewStorage.addLike(reviewLike);
    }

    public Optional<Review> addDislike(ReviewLike reviewLike) {
        return reviewStorage.addDislike(reviewLike);
    }

    public Optional<Review> removeLike(ReviewLike reviewLike) {
        return reviewStorage.removeLike(reviewLike);
    }

    public Optional<Review> updateReview(Review review) {
        return reviewStorage.update(review);
    }

    public Optional<Review> deleteReview(int reviewId) {
        return reviewStorage.delete(reviewId);
    }
}