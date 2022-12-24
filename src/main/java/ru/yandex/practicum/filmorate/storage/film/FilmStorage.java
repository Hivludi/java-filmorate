package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Film findFilmById(long id);

    Collection<Film> findAll();

}