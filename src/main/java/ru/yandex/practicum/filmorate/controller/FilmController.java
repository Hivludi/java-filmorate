package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (!FilmValidator.isValid(film)) {
            log.warn("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
        film.setId(idGenerator);
        idGenerator++;
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!FilmValidator.isValid(film)) {
            log.warn("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
        if (!films.containsKey(film.getId())) throw new RuntimeException("Фильм не существует");
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

}