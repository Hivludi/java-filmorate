package ru.yandex.practicum.filmorate.exceptions;

public class ReviewLikeAlreadyExistException extends RuntimeException {
    public ReviewLikeAlreadyExistException(String message) {
        super(message);
    }
}
