package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//связь: один к одному
@Data
public class MPA {
    Long id;

    @NotBlank(message = "Название рейтинга не может быть пустым.")
    String name;

    public MPA(long mpaId, String mpaName) {
        this.id = mpaId;
        this.name = mpaName;
    }

    public MPA() {
    }
}