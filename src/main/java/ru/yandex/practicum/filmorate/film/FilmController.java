package ru.yandex.practicum.filmorate.film;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;

import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Optional<Film> create(@Validated @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Optional<Film> update(@Validated @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Film> getFilmById(@PathVariable(value = "id") Integer filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Optional<Film> addLike(@PathVariable(value = "id") Integer filmId,
                                  @PathVariable Integer userId) {
        return filmService.addLike(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Film> removeLike(@PathVariable(value = "id") Integer filmId,
                                     @PathVariable Integer userId) {
        return filmService.removeLike(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                           @RequestParam Optional<Integer> genreId,
                                           @RequestParam Optional<Integer> year) {
        if (count < 0)
            throw new IncorrectParameterException("Количество искомых фильмов не может быть отрицательным", "count");
        if (genreId.isPresent() && genreId.get() < 0)
            throw new IncorrectParameterException("Идентификатор жанра не может быть отрицательным", "genreId");
        if (year.isPresent() && (year.get() < 1895 || year.get() > Integer.parseInt(String.valueOf(Year.now()))))
            throw new IncorrectParameterException(String.format("Год должен быть в пределах: %s-%s",
                    1895,
                    Integer.parseInt(String.valueOf(Year.now()))),
                    "year");
        return filmService.showMostPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> showCommonFilms(@RequestParam Integer userId,
                                      @RequestParam Integer friendId) {
        return filmService.showCommonFilms(userId, friendId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Integer filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findDirectorFilms(@PathVariable Integer directorId,
                                        @RequestParam(defaultValue = "likes", required = false) String sortBy) {
        if (!(sortBy.equals("year") || sortBy.equals("likes")))
            throw new IncorrectParameterException("Сортировка возможна либо по годам, либо по количеству лайков", "sortBy");
        return filmService.findDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilmsByNameOrDirector(@RequestParam String query, @RequestParam String by){
        return filmService.searchFilmsByNameOrDirector(query, by);
    }
}