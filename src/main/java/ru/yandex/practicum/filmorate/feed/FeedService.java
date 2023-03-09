package ru.yandex.practicum.filmorate.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedDao feedDao;

    public FeedEvent addFeedEvent(String eventType, String operation, Integer userId, Integer entityId) {
        FeedEvent feedEvent = FeedEvent.builder()
                .timestamp(System.currentTimeMillis())
                .eventType(eventType)
                .operation(operation)
                .userId(userId)
                .entityId(entityId)
                .build();
        return feedDao.addFeedEvent(feedEvent);
    }

    public List<FeedEvent> getUserFeed(Integer userId) {
        return feedDao.getUserFeed(userId);
    }
}
