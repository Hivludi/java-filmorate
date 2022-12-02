package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotations.NotBeforeCinemaInvented;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private int id;
    @NotBlank(message = "Name cannot be blank")
    private final String name;
    @Size(message = "Description should not exceed 200 characters", max = 200)
    private final String description;
    @NotBeforeCinemaInvented
    private final LocalDate releaseDate;
    @Min(message = "Duration cannot be lower than 0", value = 0)
    private final int duration;
}