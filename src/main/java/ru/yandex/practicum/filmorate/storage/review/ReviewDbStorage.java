package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewLikeAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.reviewLikes.ReviewLikesDao;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewLikesDao reviewLikesDao;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate, ReviewLikesDao reviewLikesDao, UserDbStorage userDbStorage, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewLikesDao = reviewLikesDao;
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Optional<Review> findReviewById(int id) {
        String sql = "select * from REVIEWS where REVIEW_ID = ?";
        Optional<Review> review = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id).stream()
                .findAny();
        if (review.isEmpty())
            throw new ObjectNotFoundException(String.format("Review с идентификатором %s не найден", id));
        return review;
    }

    @Override
    public List<Review> getReviewsByFilmId(int filmId, int count) {
        String sql = "select * from REVIEWS " +
                "where FILM_ID = ? " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count).stream().collect(Collectors.toList());
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "select * from REVIEWS " +
                "ORDER BY USEFUL DESC ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs)).stream().collect(Collectors.toList());
    }

    public Optional<Review> addLike(Integer reviewId, Integer userId) {
        ReviewLike reviewLike = ReviewLike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .isPositive(true)
                .build();
        findReviewById(reviewId);
        userDbStorage.findUserById(userId);
        Optional<ReviewLike> reviewLikeLocal = reviewLikesDao.findLikeByUserIdandReviewId(userId, reviewId);
        if (reviewLikeLocal.isPresent()) {
            throw new ReviewLikeAlreadyExistException(String.format("reviewId = \"%s\" userId = %s", reviewId, userId));
        }
        Optional<ReviewLike> reviewLikeCreated = reviewLikesDao.create(reviewLike);
        if (reviewLikeCreated.isPresent()) {
            return updateUseful(reviewLikeCreated.get().getReviewId(), reviewLikeCreated.get().isPositive());
        }
        return Optional.empty();
    }

    public Optional<Review> addDislike(Integer reviewId, Integer userId) {
        ReviewLike reviewLike = ReviewLike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .isPositive(false)
                .build();
        findReviewById(reviewId);
        userDbStorage.findUserById(userId);
        Optional<ReviewLike> reviewLikeLocal = reviewLikesDao.findLikeByUserIdandReviewId(userId, reviewId);
        if (reviewLikeLocal.isEmpty()) {
            Optional<ReviewLike> reviewLikeCreated = reviewLikesDao.create(reviewLike);
            if (reviewLikeCreated.isPresent()) {
                return updateUseful(reviewLikeCreated.get().getReviewId(), reviewLikeCreated.get().isPositive());
            }
            return Optional.empty();
        } else {
            throw new ReviewLikeAlreadyExistException(String.format("попытка повторно поставить dislike, reviewId = \"%s\" userId = %s", reviewId, userId));
        }
    }

    public Optional<Review> removeLike(Integer reviewId, Integer userId) {
        findReviewById(reviewId);
        userDbStorage.findUserById(userId);
        Optional<ReviewLike> reviewLikeLocal = reviewLikesDao.findLikeByUserIdandReviewId(userId, reviewId);
        if (reviewLikeLocal.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Review like не существует reviewId = \"%s\" userId = %s", reviewId, userId));
        }
        Optional<ReviewLike> reviewLikeDeleted = reviewLikesDao.deleteLike(reviewLikeLocal.get());
        if (reviewLikeDeleted.isPresent()) {
            return updateUseful(reviewLikeDeleted.get().getReviewId(), !reviewLikeDeleted.get().isPositive());
        }
        return Optional.empty();
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("REVIEW_ID"))
                .content(rs.getString("CONTENT"))
                .userId(rs.getInt("USER_ID"))
                .filmId(rs.getInt("FILM_ID"))
                .useful(rs.getInt("USEFUL"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .build();
    }

    @Override
    public Optional<Review> create(Review review) {
        filmDbStorage.findFilmById(review.getFilmId());
        userDbStorage.findUserById(review.getUserId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID");
        int reviewId = simpleJdbcInsert.executeAndReturnKey(toMap(review)).intValue();
        return findReviewById(reviewId);
    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("CONTENT", review.getContent());
        values.put("USER_ID", review.getUserId());
        values.put("FILM_ID", review.getFilmId());
        values.put("USEFUL", 0);
        values.put("IS_POSITIVE", review.getIsPositive());
        return values;
    }

    public Optional<Review> updateUseful(int reviewId, boolean positive) {
        Optional<Review> review = findReviewById(reviewId);
        int useful = review.get().getUseful();
        int usefulUpdated = positive ? useful + 1 : useful - 1;
        String sql = "update REVIEWS set " +
                "USEFUL = ? " +
                "where REVIEW_ID = ?";
        int amountUpdatedRows = jdbcTemplate.update(
                sql,
                usefulUpdated,
                reviewId
        );
        if (amountUpdatedRows > 0) {
            return findReviewById(reviewId);
        }
        return Optional.empty();
    }

    public Optional<Review> update(Review review) {
        findReviewById(review.getReviewId());
        String sql = "update REVIEWS set " +
                "CONTENT = ?, " +
                "IS_POSITIVE = ? " +
                "where REVIEW_ID = ?";
        int amountUpdatedRows = jdbcTemplate.update(
                sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        if (amountUpdatedRows > 0) {
            return findReviewById(review.getReviewId());
        }
        return Optional.empty();
    }

    public Optional<Review> delete(int reviewId) {
        Optional<Review> review = findReviewById(reviewId);
        String sql = "delete from REVIEWS " +
                "where REVIEW_ID = ?";
        int amountUpdatedRows = jdbcTemplate.update(
                sql,
                reviewId
        );
        if (amountUpdatedRows > 0) {
            return review;
        }
        return Optional.empty();
    }
}
