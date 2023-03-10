package ru.yandex.practicum.filmorate.exception;

public class UserAlreadyFriendedException extends RuntimeException {
    public UserAlreadyFriendedException(String message) {
        super(message);
    }
}
