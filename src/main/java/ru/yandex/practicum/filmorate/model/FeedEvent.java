package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class FeedEvent {
    private int eventId;
    private final Long timeStamp;
    @Positive(message = "Id пользователя не может быть отрицателным")
    private final Integer userId;
    private final String eventType;
    private final String operation;
    @Positive(message = "Id сущности не может быть отрицателным")
    private final Integer entityId;
}
