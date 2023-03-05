package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class Director {
    private int id;
    @Size(message = "Имя режисера должно быть длиннее трех символов", min = 3)
    private String name;
}
