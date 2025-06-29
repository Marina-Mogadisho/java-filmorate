package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

//в интерфейсе
// определены методы обеспечивающие базовые операции CRUD
public interface FilmStorage {

    public Film createFilm(Film film);

    public void deleteFilm(Long filmId);

    public Film updateFilm(Film film);

    public Optional<Film> getFilmByID(Long filmId);

    public List<Film> getAllFilms();

    public List<Film> getFilmsByMPA(Long mpaId);
}