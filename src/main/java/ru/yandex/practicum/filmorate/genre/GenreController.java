package ru.yandex.practicum.filmorate.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        return genreService.listGenre();
    }

    @GetMapping("/{id}")
    public Optional<Genre> getGenreById(@PathVariable(value = "id") int genreId) {
        return genreService.getGenreById(genreId);
    }

}
