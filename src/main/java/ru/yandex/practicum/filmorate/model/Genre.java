package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//Связь: один фильм ко многим (жанрам)
@Data
public class Genre {
    Long id;

    @NotBlank(message = "Название жанра не может быть пустым.")
    String name;

    public Genre() {
    }

    public Genre(long l, String space) {
        id = l;
        name = space;
    }
}



