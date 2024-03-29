package ru.yandex.practicum.filmorate.service.validator;

import ru.yandex.practicum.filmorate.annotation.DateInThePast;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateInThePastValidator implements ConstraintValidator<DateInThePast, LocalDate> {

    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date.isBefore(LocalDate.now());
    }

}