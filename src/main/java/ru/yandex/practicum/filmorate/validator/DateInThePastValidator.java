package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotations.DateInThePast;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateInThePastValidator implements ConstraintValidator<DateInThePast, LocalDate> {

    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date.isBefore(LocalDate.now());
    }

}