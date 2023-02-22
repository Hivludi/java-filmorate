package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.DateInThePast;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private int id;
    @NotNull(message = "У пользователя должна быть электронная почта")
    @Email(message = "Электронная почта невалидна")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(message = "Логин не может содержать пробелы", regexp = "\\S*")
    private final String login;
    private String name;
    @DateInThePast
    private final LocalDate birthday;
    private final Set<Integer> friends;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", email);
        values.put("LOGIN", login);
        if (name == null || name.isBlank()) values.put("NAME", login);
        else values.put("NAME", name);
        values.put("BIRTHDAY", birthday);
        return values;
    }
}
