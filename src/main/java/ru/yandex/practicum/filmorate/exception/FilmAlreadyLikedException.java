package ru.yandex.practicum.filmorate.exception;

public class FilmAlreadyLikedException extends RuntimeException {
    public FilmAlreadyLikedException(String message) {
        super(message);
    }
}
