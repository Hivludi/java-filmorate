package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotBeforeCinemaInventedValidatorTest {

    private Film film;

    @BeforeEach
    public void setUp() {
        film = Film
                .builder()
                .id(1)
                .name("testName")
                .description("testDescription")
                .releaseDate(LocalDate.of(1990, 11, 21))
                .duration(10)
                .build();
    }

    @Test
    public void releaseDateShouldNotBeBeforeCinemaInvention() {
        assertTrue(film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28)));
        film = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();
        assertFalse(film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28)));
    }
}
