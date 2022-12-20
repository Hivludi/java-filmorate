package ru.yandex.practicum.filmorate.exceptions;

public class UserAlreadyFriendedException extends RuntimeException {
    public UserAlreadyFriendedException(String message) {
        super(message);
    }
}
