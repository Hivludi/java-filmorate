package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class FilmValidator {

    public static boolean isValid(Film film) {
        return !film.getName().isBlank() &&
                film.getDescription().length() <= 200 &&
                !film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) &&
                film.getDuration() >= 0;
    }

}