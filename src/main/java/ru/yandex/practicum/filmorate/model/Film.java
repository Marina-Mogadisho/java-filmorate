package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    Set<Long> likeUser = new HashSet<>(); // Список id пользователей, которые поставили лайки этому фильму
    Long like = 0L; // Количество лайков у этого фильма
}
