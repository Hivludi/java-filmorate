package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FeedService feedService;

    public Optional<Review> create(Review review) {
        Optional<Review> reviewOptional = reviewStorage.create(review);
        if (reviewOptional.isPresent()) {
            Review reviewCreated = reviewOptional.get();
            feedService.addFeedEvent("REVIEW", "ADD", reviewCreated.getUserId(),
                    reviewCreated.getReviewId());
        }
        return reviewOptional;
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

    public Optional<Review> addLike(Integer reviewId, Integer userId) {
        return reviewStorage.addLike(reviewId, userId);
    }

    public Optional<Review> addDislike(Integer reviewId, Integer userId) {
        return reviewStorage.addDislike(reviewId, userId);
    }

    public Optional<Review> removeLike(Integer reviewId, Integer userId) {
        return reviewStorage.removeLike(reviewId, userId);
    }

    public Optional<Review> updateReview(Review review) {
        Optional<Review> reviewOptional = reviewStorage.update(review);
        if (reviewOptional.isPresent()) {
            Review reviewUpdated = reviewOptional.get();
            feedService.addFeedEvent("REVIEW", "UPDATE", reviewUpdated.getUserId(),
                    reviewUpdated.getReviewId());
        }
        return reviewOptional;
    }

    public Optional<Review> deleteReview(int reviewId) {
        Optional<Review> opt = findReviewById(reviewId);
        if (opt.isPresent()) {
            Review review = opt.get();
            feedService.addFeedEvent("REVIEW", "REMOVE", review.getUserId(), review.getReviewId());
        }

        return reviewStorage.delete(reviewId);
    }
}