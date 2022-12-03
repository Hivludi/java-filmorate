package ru.yandex.practicum.filmorate.annotations;

import ru.yandex.practicum.filmorate.validator.DateInThePastValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateInThePastValidator.class)
public @interface DateInThePast {
    String message() default "Birthday cannot be in future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}