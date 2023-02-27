package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService (@Qualifier("FilmDB") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Optional<Film> addLike(int userId, int filmId) {
        return filmStorage.addLike(userId, filmId);
    }

    public Optional<Film> removeLike(int userId, int filmId) {
        return filmStorage.removeLike(userId, filmId);
    }

    public List<Film> showMostPopularFilms(Integer count) {
        return filmStorage.showMostPopularFilms(count);
    }

    public Optional<Film> create(Film film) {
        return filmStorage.create(film);
    }

    public Optional<Film> update(Film film) {
        return filmStorage.update(film);
    }

    public Optional<Film> findFilmById(int id) {
        return filmStorage.findFilmById(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }
}