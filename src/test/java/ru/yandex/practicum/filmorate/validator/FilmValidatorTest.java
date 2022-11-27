package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidatorTest {

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
    public void nameShouldNotBeEmptyOrBlank() {
        assertTrue(FilmValidator.isValid(film));
        film = film.toBuilder().name("").build();
        assertFalse(FilmValidator.isValid(film));
        film = film.toBuilder().name("      ").build();
        assertFalse(FilmValidator.isValid(film));
    }

    @Test
    public void descriptionShouldNotBeMoreThan200CharsSize() {
        assertTrue(FilmValidator.isValid(film));
        String str = new String(new char[200]);
        str = str.replace("\0", "a");
        film = film.toBuilder().description(str).build();
        assertTrue(FilmValidator.isValid(film));
        String str2 = new String(new char[201]);
        str2 = str2.replace("\0", "a");
        film = film.toBuilder().description(str2).build();
        assertFalse(FilmValidator.isValid(film));
    }

    @Test
    public void releaseDateShouldNotBeBefore28December1895() {
        assertTrue(FilmValidator.isValid(film));
        film = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 28)).build();
        assertTrue(FilmValidator.isValid(film));
        film = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 28).minusDays(1)).build();
        assertFalse(FilmValidator.isValid(film));
    }

    @Test
    public void filmDurationShouldBePositive() {
        assertTrue(FilmValidator.isValid(film));
        film = film.toBuilder().duration(0).build();
        assertTrue(FilmValidator.isValid(film));
        film = film.toBuilder().duration(-1).build();
        assertFalse(FilmValidator.isValid(film));
    }
}
