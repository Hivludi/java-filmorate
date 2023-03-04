package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FeedService feedService;

    public FilmService (@Qualifier("FilmDB") FilmStorage filmStorage, FeedService feedService) {
        this.filmStorage = filmStorage;
        this.feedService = feedService;
    }

    public Optional<Film> addLike(int userId, int filmId) {
        feedService.addFeedEvent("LIKE", "ADD", userId, filmId);
        return filmStorage.addLike(userId, filmId);
    }

    public Optional<Film> removeLike(int userId, int filmId) {
        filmStorage.removeLike(userId, filmId);
        feedService.addFeedEvent("LIKE", "REMOVE", userId, filmId);
        return filmStorage.removeLike(userId, filmId);
    }

    public List<Film> showMostPopularFilms(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        return filmStorage.showMostPopularFilms(count, genreId, year);
    }

    public List<Film> showCommonFilms(int userId, int friendId) {
        return filmStorage.showCommonFilms(userId, friendId);
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

    public void deleteFilmById(Integer filmId) {
        filmStorage.deleteFilmById(filmId);
    }

    public List<Film> findDirectorFilms(int directorId, String sortBy) {return filmStorage.findDirectorFilms(directorId, sortBy);}
}