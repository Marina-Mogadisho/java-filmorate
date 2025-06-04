package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//связь: один к одному
@Data
public class MPA {
    Long id;

    @NotBlank(message = "Название рейтинга не может быть пустым.")
    String name;

    public MPA(long mpa_id, String mpa_name) {
        this.id = mpa_id;
        this.name = mpa_name;
    }

    public MPA() {
    }
}
