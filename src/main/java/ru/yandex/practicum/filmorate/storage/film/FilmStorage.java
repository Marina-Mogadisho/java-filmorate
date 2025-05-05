package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

//в интерфейсе
// определены методы добавления, удаления и модификации объектов.
public interface FilmStorage {
    public List<Film> findAll();

    public Film create(Film film) throws Throwable;

    public Film update(Film newFilm) throws Throwable;

    public Set<Long> getAllIdFilms();

    public Film getFilm(Long idFilm);

    public Film addLike(Long idFilm);

    public Film deleteLike(Long idFilm);
}
