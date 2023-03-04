package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Optional<Film> create(@Validated @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Optional<Film> update(@Validated @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Film> getFilmById(@PathVariable(value = "id") Integer filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Optional<Film> addLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        return filmService.addLike(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Film> removeLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        return filmService.removeLike(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                           @RequestParam Optional<Integer> genreId,
                                           @RequestParam Optional<Integer> year) {
        if (count < 0) throw new IncorrectParameterException("Количество искомых фильмов не может быть отрицательным", "count");
        if (genreId.isPresent() && genreId.get() < 0) throw new IncorrectParameterException("Идентификатор жанра не может быть отрицательным", "genreId");
        if (year.isPresent() && (year.get() < 1895 || year.get() > Integer.parseInt(String.valueOf(Year.now())))) throw new IncorrectParameterException(String.format("Год должен быть в пределах: %s-%s", 1895, Integer.parseInt(String.valueOf(Year.now()))), "year");
        return filmService.showMostPopularFilms(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable(value = "filmId") Integer filmId) {
        filmService.deleteFilmById(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable(value = "filmId") Integer filmId) {
        filmService.deleteFilmById(filmId);
    }
}