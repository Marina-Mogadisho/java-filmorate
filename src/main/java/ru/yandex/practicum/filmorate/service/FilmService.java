package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Calendar.DECEMBER;

//будет отвечать за операции с фильмами —
// добавление и удаление лайка, вывод 10
// наиболее популярных фильмов по количеству лайков.
@Service //к ним можно будет получить доступ из контроллера.
public class FilmService {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate movieInventDate = LocalDate.of(1895, DECEMBER, 20);

    //класс зависимость
    @Autowired
    UserService userService;

    //@GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    // @PostMapping
    public Film create(Film film) {
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


    // @PutMapping
    public Film update(Film newFilm) {
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
