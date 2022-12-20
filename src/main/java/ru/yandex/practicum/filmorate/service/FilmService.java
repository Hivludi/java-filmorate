package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class FilmService {

    private final FilmStorage filmStorage;

    public void addLike(long userId, long filmId) {
        Set<Long> filmLikes = filmStorage.findFilmById(filmId).getLikes();
        if (filmLikes.contains(userId)) throw new FilmAlreadyLikedException("Фильм уже содержит лайк от данного пользователя");
        filmLikes.add(userId);
    }

    public void removeLike(long userId, long filmId) {
        Set<Long> filmLikes = filmStorage.findFilmById(filmId).getLikes();
        if (!filmLikes.contains(userId)) throw new LikeNotFoundException("Фильм не содержит лайк от данного пользователя");
        filmLikes.remove(userId);
    }

    public List<Film> showMostPopularFilms(Integer count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size() * -1))
                .limit(count)
                .collect(Collectors.toList());
    }
}