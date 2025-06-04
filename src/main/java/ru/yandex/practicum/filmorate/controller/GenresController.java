package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmDbService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;
import java.util.Optional;

@Validated
@Slf4j
@RestController
@RequestMapping("/genres")

public class GenresController {
    FilmDbService filmDbService;
    FilmDbStorage filmDbStorage;

    @Autowired
    public GenresController(FilmDbService filmDbService, FilmDbStorage filmDbStorage) {
        this.filmDbService = filmDbService;
        this.filmDbStorage = filmDbStorage;
    }

    //Get /genres
    @GetMapping
    public List<Genre> getAllGenre() {
        return filmDbService.getAllGenres();
    }

    //Get /genres/{genreId} —
    @GetMapping("{genreId}")
    public Optional<Genre> getGenreById(@PathVariable Long genreId) {
        return filmDbService.getGenreById(genreId);
    }
}


