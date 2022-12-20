package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Data
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public Film create(Film film) {
        film.setId(idGenerator);
        idGenerator++;
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) throw new FilmNotFoundException("Фильм не существует");
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findFilmById(long id) {
        if (films.get(id) == null) throw new FilmNotFoundException("Фильм не существует");
        return films.get(id);
    }
}
