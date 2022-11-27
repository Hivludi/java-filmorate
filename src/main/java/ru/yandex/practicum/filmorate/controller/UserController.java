package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 1;

    @PostMapping
    public User create(@RequestBody User user) {
        if (!UserValidator.isValid(user)) {
            log.warn("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
        user.setId(idGenerator);
        idGenerator++;
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!UserValidator.isValid(user)) {
            log.warn("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
        if (!users.containsKey(user.getId())) throw new RuntimeException("Фильм не существует");
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

}