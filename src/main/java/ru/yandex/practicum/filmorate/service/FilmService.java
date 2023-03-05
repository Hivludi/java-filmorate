package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("FilmDB") FilmStorage filmStorage) {
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

    public List<Film> searchFilmsByNameOrDirector(String query, String by) {
        String[] bySplit = by.split(",");
        String queryToLowerCase = "%" + query.toLowerCase() + "%";
        if (bySplit.length > 2 || (!Arrays.asList(bySplit).contains("title")
                && !Arrays.asList(bySplit).contains("director"))) {
            throw new IncorrectParameterException(by, " параметр должен принимать значения director, " +
                    "title или director, title.");
        } else if (bySplit.length == 2) {
            return filmStorage.searchFilmsByNameOrDirector(queryToLowerCase);
        } else {
            switch (bySplit[0]) {
                case "title":
                    return filmStorage.searchFilmsByName(queryToLowerCase);
                case "director":
                    return filmStorage.searchFilmsByDirector(queryToLowerCase);
                default:
                    throw new IncorrectParameterException(by, " параметр должен принимать значения director, " +
                            "title или director, title.");
            }
        }
    }
}