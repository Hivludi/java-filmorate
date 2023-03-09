package ru.yandex.practicum.filmorate.genre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Genre {
    private int id;
    private String name;
}
