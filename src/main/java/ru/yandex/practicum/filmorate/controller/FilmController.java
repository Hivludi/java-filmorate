package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film create(@Validated @RequestBody Film film) {
        return filmService.getFilmStorage().create(film);
    }

    @PutMapping
    public Film update(@Validated @RequestBody Film film) {
        return filmService.getFilmStorage().update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getFilmStorage().findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(value = "id") Long filmId) {
        return filmService.getFilmStorage().findFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Long filmId, @PathVariable Long userId) {
        filmService.addLike(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable(value = "id") Long filmId, @PathVariable Long userId) {
        filmService.removeLike(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count < 0) throw new IncorrectParameterException("Количество искомых фильмов не может быть отрицательным", "count");
        return filmService.showMostPopularFilms(count);
    }
}