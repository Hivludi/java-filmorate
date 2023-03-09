package ru.yandex.practicum.filmorate.mpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Mpa {
    private int id;
    private String name;
}
