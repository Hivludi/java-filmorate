package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Film findFilmById(long id);

    Collection<Film> findAll();

}