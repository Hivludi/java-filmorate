package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {

    public static boolean isValid(User user) {
        return !user.getEmail().isBlank() &&
                user.getEmail().contains("@") &&
                !user.getLogin().isBlank() &&
                !user.getLogin().contains(" ") &&
                !user.getBirthday().isAfter(LocalDate.now());
    }

}