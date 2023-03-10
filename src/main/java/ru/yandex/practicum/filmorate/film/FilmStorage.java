package ru.yandex.practicum.filmorate.film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> findFilmById(int id);

    Collection<Film> findAll();

    Optional<Film> addLike(int userId, int filmId);

    Optional<Film> removeLike(int userId, int filmId);

    List<Film> showMostPopularFilms(Integer count, Optional<Integer> genreId, Optional<Integer> year);

    List<Film> showCommonFilms(int userId, int friendId);

    void deleteFilmById(Integer filmId);

    Collection<Film> showRecommendations(Integer userId);

    List<Film> findDirectorFilms(int directorId, String sortBy);

    List<Film> searchFilmsByNameOrDirector(String query);

    List<Film> searchFilmsByName(String query);

    List<Film> searchFilmsByDirector(String query);

}