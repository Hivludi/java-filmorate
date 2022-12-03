package ru.yandex.practicum.filmorate.annotations;

import ru.yandex.practicum.filmorate.validator.NotBeforeCinemaInventedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBeforeCinemaInventedValidator.class)
public @interface NotBeforeCinemaInvented {
    String message() default "Film release date cannot be before cinema is invented";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}