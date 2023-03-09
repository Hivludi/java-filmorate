package ru.yandex.practicum.filmorate.storage.reviewLikes;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewLikesDao {

    private final JdbcTemplate jdbcTemplate;

    public Optional<ReviewLike> findLikeByUserIdAndReviewId(int userId, int reviewId) {
        return jdbcTemplate
                .query("SELECT * FROM review_likes WHERE user_id = ? AND review_id = ?",
                        (rs, rowNum) -> makeReviewLikes(rs), userId, reviewId)
                .stream()
                .findAny();
    }

    public Optional<ReviewLike> updateLike(ReviewLike reviewLike) {
        String sql = "UPDATE review_likes SET " +
                "is_positive = ? " +
                "WHERE review_id = ? AND user_id = ?";
        int amountUpdatedRows = jdbcTemplate.update(
                sql,
                reviewLike.isPositive(),
                reviewLike.getReviewId(),
                reviewLike.getUserId()
        );
        if (amountUpdatedRows > 0) {
            return findLikeByUserIdAndReviewId(reviewLike.getUserId(), reviewLike.getReviewId());
        }
        return Optional.empty();
    }

    public Optional<ReviewLike> deleteLike(ReviewLike reviewLike) {
        String sql = "DELETE FROM review_likes " +
                "WHERE review_id = ? AND user_id = ?";
        int amountUpdatedRows = jdbcTemplate.update(
                sql,
                reviewLike.getReviewId(),
                reviewLike.getUserId()
        );
        if (amountUpdatedRows > 0) {
            return Optional.of(reviewLike);
        }
        return Optional.empty();
    }


    public Optional<ReviewLike> create(ReviewLike reviewLike) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEW_LIKES");
        int reviewId = simpleJdbcInsert.execute(toMap(reviewLike));
        return findLikeByUserIdAndReviewId(reviewLike.getUserId(), reviewLike.getReviewId());
    }

    private Map<String, Object> toMap(ReviewLike reviewLike) {
        Map<String, Object> values = new HashMap<>();
        values.put("REVIEW_ID", reviewLike.getReviewId());
        values.put("USER_ID", reviewLike.getUserId());
        values.put("IS_POSITIVE", reviewLike.isPositive());
        return values;
    }

    private ReviewLike makeReviewLikes(ResultSet rs) throws SQLException {
        return ReviewLike.builder()
                .reviewId(rs.getInt("REVIEW_ID"))
                .userId(rs.getInt("USER_ID"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .build();
    }
}
