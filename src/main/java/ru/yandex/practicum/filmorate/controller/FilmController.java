package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import static java.util.Calendar.DECEMBER;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate movieInventDate = LocalDate.of(1895, DECEMBER, 20);

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        // проверяем выполнение необходимых условий
        checkReleaseDate(film);
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        return film;
    }

    // вспомогательный метод для генерации идентификатора нового фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            throw new ValidationException("Не могу обновить, такого Id фильма не существует.");
        }
        checkReleaseDate(newFilm);
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(movieInventDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }
}


