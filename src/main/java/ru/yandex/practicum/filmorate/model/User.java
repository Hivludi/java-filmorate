package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.annotations.DateInThePast;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private long id;
    @NotNull(message = "У пользователя должна быть электронная почта")
    @Email(message = "Электронная почта невалидна")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(message = "Логин не может содержать пробелы", regexp = "\\S*")
    private final String login;
    private String name;
    @DateInThePast
    private final LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}
