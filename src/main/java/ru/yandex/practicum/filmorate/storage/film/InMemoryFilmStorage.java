package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import static java.util.Calendar.DECEMBER;

//@Component аннотация, которая определяет класс как управляемый
// Spring. Такой класс будет
// добавлен в контекст приложения при сканировании.

//вся логика хранения, обновления и поиска объектов.
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate movieInventDate = LocalDate.of(1895, DECEMBER, 20);


    //@GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Set<Long> getAllIdFilms() {
        return films.keySet();
    }

    public Film addLike(Long idFilm) {
        Film film = films.get(idFilm);
        Long l = film.getLike();
        l = l + 1;
        film.setLike(l);
        return film;

    }

    public Film deleteLike(Long idFilm) {
        Film film = films.get(idFilm);
        Long l = film.getLike();
        l--;
        film.setLike(l);
        return film;
    }

    // @PostMapping
    public Film create(Film film) throws Throwable {
        // проверяем выполнение необходимых условий
        checkReleaseDate(film);
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        return film;
    }

    // @PutMapping
    public Film update(Film newFilm) throws Throwable {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Не могу обновить, такого Id фильма не существует.");
        }
        checkReleaseDate(newFilm);
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    public Film getFilm(Long idFilm) {
        return films.get(idFilm);
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

    private void checkReleaseDate(Film film) throws Throwable {
        if (film.getReleaseDate().isBefore(movieInventDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }
}
