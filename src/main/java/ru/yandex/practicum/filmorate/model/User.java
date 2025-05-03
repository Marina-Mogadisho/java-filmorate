package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    Long id;

    @NotBlank(message = "Емейл должен быть указан.")
    @Email(message = "Емейл должен быть указан.")
    String email;

    @NotBlank(message = "Логин не может быть пустым.")
    String login;

    String name;

    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull(message = "Дата рождения не может пустой")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    LocalDate birthday;

    Set<Long> friends; // список id друзей
}
