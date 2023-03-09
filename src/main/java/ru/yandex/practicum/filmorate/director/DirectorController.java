package ru.yandex.practicum.filmorate.director;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.listDirector();
    }

    @GetMapping("/{id}")
    public Optional<Director> getDirectorById(@PathVariable int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Optional<Director> create(@Validated @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Optional<Director> update(@Validated @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(value = "id") int directorId) {
        directorService.delete(directorId);
    }

}
