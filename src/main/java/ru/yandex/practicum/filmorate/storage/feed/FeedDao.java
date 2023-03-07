package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeedDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FeedDao(JdbcTemplate jdbcTemplate, @Qualifier("UserDB") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public FeedEvent addFeedEvent(FeedEvent feedEvent) {
        if (userStorage.findUserById(feedEvent.getUserId()).isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с таким id нет в базе!");
        }
        if (!isEntityExist(feedEvent)) {
            throw new ObjectNotFoundException("Сущности с таким id нет в базе!");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID");
        feedEvent.setEventId(simpleJdbcInsert.executeAndReturnKey(toMap(feedEvent)).intValue());
        return feedEvent;
    }

    public List<FeedEvent> getUserFeed(int userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с таким id нет в базе!");
        }
        return jdbcTemplate.query("select * from EVENTS where USER_ID = ?", (rs, rowNum) -> makeEvent(rs), userId);
    }

    private FeedEvent makeEvent(ResultSet rs) throws SQLException {
        return FeedEvent.builder()
                .eventId(rs.getInt("EVENT_ID"))
                .timestamp(rs.getLong("TIMESTAMP"))
                .userId(rs.getInt("USER_ID"))
                .eventType(rs.getString("EVENT_TYPE"))
                .operation(rs.getString("OPERATION"))
                .entityId(rs.getInt("ENTITY_ID"))
                .build();
    }

    private Map<String, Object> toMap(FeedEvent feedEvent) {
        Map<String, Object> values = new HashMap<>();
        values.put("TIMESTAMP", feedEvent.getTimestamp());
        values.put("USER_ID", feedEvent.getUserId());
        values.put("EVENT_TYPE", feedEvent.getEventType());
        values.put("OPERATION", feedEvent.getOperation());
        values.put("ENTITY_ID", feedEvent.getEntityId());
        return values;
    }

    private boolean isEntityExist(FeedEvent feedEvent) {
        SqlRowSet rs;
        switch (feedEvent.getEventType()) {
            case ("LIKE"):
                rs = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?",
                        feedEvent.getEntityId());
                return rs.next();
            case ("REVIEW"):
                rs = jdbcTemplate.queryForRowSet("select * from REVIEWS where REVIEW_ID = ?",
                        feedEvent.getEntityId());
                return rs.next();
            default:
                return userStorage.findUserById(feedEvent.getEntityId()).isPresent();
        }
    }
}
