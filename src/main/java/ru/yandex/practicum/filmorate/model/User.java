package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.annotations.DateInThePast;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private int id;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email is not Valid")
    private final String email;
    @NotBlank(message = "Login cannot be blank")
    @Pattern(message = "Login cannot contain whitespaces", regexp = "\\S*")
    private final String login;
    private String name;
    @DateInThePast
    private final LocalDate birthday;
}
