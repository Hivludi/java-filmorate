package ru.yandex.practicum.filmorate.exceptions;

public class FilmAlreadyLikedException extends RuntimeException {
    public FilmAlreadyLikedException(String message) {
        super(message);
    }
}
