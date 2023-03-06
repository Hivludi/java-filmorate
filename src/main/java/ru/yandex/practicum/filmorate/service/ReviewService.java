package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FeedService feedService;

    public ReviewService(ReviewStorage reviewStorage, FeedService feedService) {
        this.feedService = feedService;
        this.reviewStorage = reviewStorage;
    }

    public Optional<Review> create(Review review) {
        Optional<Review> opt = reviewStorage.create(review);
        if (opt.isPresent()) {
            Review review1 = opt.get();
            feedService.addFeedEvent("REVIEW", "ADD", review1.getUserId(), review1.getReviewId());
        }
        return opt;
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
        feedService.addFeedEvent("REVIEW", "UPDATE", review.getUserId(), review.getReviewId());
        return reviewStorage.update(review);
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