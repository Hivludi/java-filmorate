package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotations.NotBeforeCinemaInvented;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class NotBeforeCinemaInventedValidator implements ConstraintValidator<NotBeforeCinemaInvented, LocalDate> {

    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        LocalDate cinemaInventedDate = LocalDate.of(1895, 12, 28);
        return date.isAfter(cinemaInventedDate);
    }

}