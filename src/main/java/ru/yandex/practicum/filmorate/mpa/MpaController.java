package ru.yandex.practicum.filmorate.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        return mpaService.listMpa();
    }

    @GetMapping("/{id}")
    public Optional<Mpa> getMpaById(@PathVariable(value = "id") int mpaId) {
        return mpaService.getMpaById(mpaId);
    }

}
