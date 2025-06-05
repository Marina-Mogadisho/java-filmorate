package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import java.util.List;
import java.util.Optional;

@Validated
@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final FilmDbService filmDbService;

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


