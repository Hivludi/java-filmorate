package ru.yandex.practicum.filmorate.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewLikeAlreadyExistException;
import ru.yandex.practicum.filmorate.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.reviewlikes.ReviewLike;
import ru.yandex.practicum.filmorate.reviewlikes.ReviewLikesDao;
import ru.yandex.practicum.filmorate.user.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewLikesDao reviewLikesDao;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Override
    public Optional<Review> findReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        Optional<Review> review = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id).stream()
                .findAny();
        if (review.isEmpty())
            throw new ObjectNotFoundException(String.format("Review с идентификатором %s не найден", id));
        return review;
    }

    @Override
    public List<Review> getReviewsByFilmId(int filmId, int count) {
        String sql = "SELECT * FROM reviews " +
                "WHERE film_id = ? " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count).stream().collect(Collectors.toList());
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM reviews " +
                "ORDER BY useful DESC ";
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
        Optional<ReviewLike> reviewLikeLocal = reviewLikesDao.findLikeByUserIdAndReviewId(userId, reviewId);
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
        Optional<ReviewLike> reviewLikeLocal = reviewLikesDao.findLikeByUserIdAndReviewId(userId, reviewId);
        if (reviewLikeLocal.isEmpty()) {
            Optional<ReviewLike> reviewLikeCreated = reviewLikesDao.create(reviewLike);
            if (reviewLikeCreated.isPresent()) {
                return updateUseful(reviewLikeCreated.get().getReviewId(), reviewLikeCreated.get().isPositive());
            }
            return Optional.empty();
        } else {
            throw new ReviewLikeAlreadyExistException(
                    String.format("попытка повторно поставить dislike, reviewId = \"%s\" userId = %s", reviewId, userId));
        }
    }

    public Optional<Review> removeLike(Integer reviewId, Integer userId) {
        findReviewById(reviewId);
        userDbStorage.findUserById(userId);
        Optional<ReviewLike> reviewLikeLocal = reviewLikesDao.findLikeByUserIdAndReviewId(userId, reviewId);
        if (reviewLikeLocal.isEmpty()) {
            throw new ObjectNotFoundException(
                    String.format("Review like не существует reviewId = \"%s\" userId = %s", reviewId, userId));
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
        String sql = "UPDATE reviews SET " +
                "useful = ? " +
                "WHERE review_id = ?";
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
        String sql = "UPDATE reviews SET " +
                "content = ?, " +
                "is_positive = ? " +
                "WHERE review_id = ?";
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
        String sql = "DELETE FROM reviews " +
                "WHERE review_id = ?";
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
