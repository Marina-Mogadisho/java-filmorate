package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;
import java.util.Optional;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmDbService filmDbService;
    private final FilmDbStorage filmDbStorage;

    //GET/films - вывести все фильмы
    @GetMapping
    public List<Film> findAll() {
        return filmDbStorage.getAllFilms();
    }

    //POST/films - создать фильм
    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        return filmDbStorage.createFilm(film);
    }

    //PUT/films - обновить фильм, данные в теле запроса
    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        return filmDbStorage.updateFilm(newFilm);
    }

    //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    @PutMapping("/{id}/like/{userId}")
    public Optional<Film> addLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmDbService.addLikeToFilm(id, userId);
    }

    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Film> delete(@PathVariable Long id, @PathVariable Long userId) {
        return filmDbService.deleteLikeToFilm(id, userId);
    }

    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    //@GetMapping("/popular?count={count}")
    @GetMapping("/popular")
    public List<Film> getListFilmsOFLike(@RequestParam(required = false, defaultValue = "10")
                                         @Positive Integer count) {
        List<Film> l = filmDbService.getListFilmsPopular(count);
        //List<Long> l = filmDbService.getListFilmsPopular(count);
        return l;
    }


    //GET/films/{id} - вывести фильм по его ID
    @GetMapping("/{id}")
    public Optional<Film> getFilmByID(@PathVariable Long id) {
        return filmDbStorage.getFilmByID(id);
    }
}


