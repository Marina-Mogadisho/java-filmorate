package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    Long id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    String name;

    @Size(min = 1, max = 200, message = "Длина описания фильма не может быть больше 200 символов")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    int duration;
}
