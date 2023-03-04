package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeedDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public FeedEvent addFeedEvent(FeedEvent feedEvent) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID");
        feedEvent.setEventId(simpleJdbcInsert.executeAndReturnKey(toMap(feedEvent)).intValue());
        return feedEvent;
    }

    public List<FeedEvent> getUserFeed(int userId) {
        return jdbcTemplate.query("select * from EVENTS where USER_ID = ?", (rs, rowNum) -> makeEvent(rs), userId);
    }

    private FeedEvent makeEvent(ResultSet rs) throws SQLException {
        return FeedEvent.builder()
                .eventId(rs.getInt("EVENT_ID"))
                .timeStamp(rs.getLong("TIMESTAMP"))
                .userId(rs.getInt("USER_ID"))
                .eventType(rs.getString("EVENT_TYPE"))
                .operation(rs.getString("OPERATION"))
                .entityId(rs.getInt("ENTITY_ID"))
                .build();
    }

    private Map<String, Object> toMap(FeedEvent feedEvent) {
        Map<String, Object> values = new HashMap<>();
        values.put("TIMESTAMP", feedEvent.getTimeStamp());
        values.put("USER_ID", feedEvent.getUserId());
        values.put("EVENT_TYPE", feedEvent.getEventType());
        values.put("OPERATION", feedEvent.getOperation());
        values.put("ENTITY_ID", feedEvent.getEntityId());
        return values;
    }
}
