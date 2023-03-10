package ru.yandex.practicum.filmorate.exception;

public class ReviewLikeAlreadyExistException extends RuntimeException {
    public ReviewLikeAlreadyExistException(String message) {
        super(message);
    }
}
