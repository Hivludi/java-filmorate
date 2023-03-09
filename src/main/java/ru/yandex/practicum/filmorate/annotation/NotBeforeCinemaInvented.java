package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.service.validator.NotBeforeCinemaInventedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBeforeCinemaInventedValidator.class)
public @interface NotBeforeCinemaInvented {
    String message() default "Дата релиза фильма не может быть раньше даты изобретения кино";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}