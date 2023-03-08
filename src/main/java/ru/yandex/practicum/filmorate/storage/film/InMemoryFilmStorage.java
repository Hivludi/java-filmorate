package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return findAll().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size() * -1))
                .filter(f -> year.isEmpty() || f.getReleaseDate().getYear() == year.get())
                .filter(f -> genreId.isEmpty() || f.getGenres().stream().map(Genre::getId).anyMatch(i -> i.equals(genreId.get())))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> showCommonFilms(int userId, int friendId) {
        return findAll().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size() * -1))
                .filter(f -> f.getLikes().containsAll(List.of(userId, friendId)))
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
        // Все пользователи которым также нравится те фильмы за исключением пользователя, которому нужна рекомендация
        Set<Integer> otherUserIds = showFilmsUserLikes(userId).stream()
                .collect(HashSet::new,
                        (collection, film) ->
                                collection.addAll(film.getLikes()),
                        Collection::addAll);

        otherUserIds.remove(userId);

        // Все понравившиеся фильмы, всех пользователей за исключением фильмов пользователя, которому нужна рекомендация
        return films.values().stream()
                .collect(HashSet::new,
                        (collection, film) ->
                                otherUserIds.forEach(id -> {
                                    if (film.getLikes().contains(id)) {
                                        collection.add(film);
                                    }
                                }),
                        Collection::addAll);
    }

    @Override
    public List<Film> searchFilmsByNameOrDirector(String query) {
        return Stream.of(searchFilmsByName(query), searchFilmsByDirector(query))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchFilmsByName(String query) {
        return new ArrayList<>(findAll()).stream()
                .filter(film -> film.getName().contains(query))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        return new ArrayList<>(findAll()).stream()
                .filter(film -> film.getName().contains(query))
                .collect(Collectors.toList());
    }
}
