package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.NotBeforeCinemaInvented;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private int id;
    @NotBlank(message = "Имя не может быть пустым")
    private final String name;
    @Size(message = "Описание фильма не должно превышать 200 символов", max = 200)
    private final String description;
    @NotBeforeCinemaInvented
    private final LocalDate releaseDate;
    @Min(message = "Длительность фильма не может быть меньше 0", value = 0)
    private final int duration;
    private final Set<Integer> likes;
    private final Mpa mpa;
    private final Set<Genre> genres;
    private final List<Director> directors;
}