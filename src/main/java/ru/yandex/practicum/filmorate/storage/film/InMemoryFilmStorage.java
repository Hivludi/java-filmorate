package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component("FilmInMemory")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public Optional<Film> create(Film film) {
        film.setId(idGenerator);
        idGenerator++;
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> update(Film film) {
        if (!films.containsKey(film.getId())) throw new ObjectNotFoundException("Фильм не существует");
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return Optional.of(film);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        if (films.get(id) == null) throw new ObjectNotFoundException("Фильм не существует");
        return Optional.of(films.get(id));
    }

    @Override
    public Optional<Film> addLike(int userId, int filmId) {
        Set<Integer> filmLikes = findFilmById(filmId).get().getLikes();
        if (filmLikes.contains(userId))
            throw new FilmAlreadyLikedException("Фильм уже содержит лайк от данного пользователя");
        filmLikes.add(userId);
        return findFilmById(filmId);
    }

    @Override
    public Optional<Film> removeLike(int userId, int filmId) {
        Set<Integer> filmLikes = findFilmById(filmId).get().getLikes();
        if (!filmLikes.contains(userId))
            throw new LikeNotFoundException("Фильм не содержит лайк от данного пользователя");
        filmLikes.remove(userId);
        return findFilmById(filmId);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Film> mostPopularFilms = new ArrayList<>();
        findAll().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size() * -1))
                .forEach(f0 -> {
                    if (genreId.isPresent() && year.isPresent()) {
                        if (f0.getGenres().contains(genreId) && f0.getReleaseDate().getYear() == year.get()) mostPopularFilms.add(f0);
                    } else if (genreId.isPresent()) {
                        if (f0.getGenres().contains(genreId)) mostPopularFilms.add(f0);
                    } else if (year.isPresent()) {
                        if (f0.getReleaseDate().getYear() == year.get()) mostPopularFilms.add(f0);
                    } else {
                        mostPopularFilms.add(f0);
                    }
                });
        return mostPopularFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilmById(Integer filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId, films.get(filmId));
            log.info("Фильм с id= " + filmId + " удалён");
        } else {
            throw new ObjectNotFoundException("Фильма с id= " + filmId + " не существует");
        }
    }

    public Collection<Film> showFilmsUserLikes(Integer userId) {
        Collection<Film> filmsUserLikes = new ArrayList<>();
        films.forEach((integer, film) -> {
            if (film.getLikes().contains(userId)) {
                filmsUserLikes.add(film);
            }
        });
        return filmsUserLikes;
    }

    @Override
    public Collection<Film> showRecommendations(Integer userId) {
        Set<Integer> otherUsersSamePreferences = new HashSet<>();

        // Все пользователи которым также нравится те фильмы за исключением пользователя, которому нужна рекомендация
        showFilmsUserLikes(userId)
                .forEach(film ->
                        otherUsersSamePreferences.addAll(film.getLikes()));
        otherUsersSamePreferences.remove(userId);

        // Все понравившиеся фильмы, всех тех пользователей за исключением фильмов пользователя, которому нужна рекомендация
        // отсортированные по колличеству лайков и готовый к показу
        return films.values().stream()
                .parallel()
                .collect(() -> new TreeSet<>(Comparator.comparing(film -> film.getLikes().size() * -1)),
                        (collection, film) ->
                                otherUsersSamePreferences.forEach(id -> {
                                    if (film.getLikes().contains(id)) {
                                        collection.add(film);
                                    }
                                }),
                        Collection::addAll);
    }

}
