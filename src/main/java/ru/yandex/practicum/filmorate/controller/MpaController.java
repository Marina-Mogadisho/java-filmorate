package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import java.util.List;
import java.util.Optional;

@Validated
@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final FilmDbService filmDbService;

    @GetMapping
    public List<MPA> findAll() {
        return filmDbService.getAllMpas();
    }

    //Get /mpa/{userId} — пользователь ставит лайк фильму.
    @GetMapping("/{mpaId}")
    public Optional<MPA> getMpa(@PathVariable Long mpaId) {
        return filmDbService.getMpa(mpaId);
    }
}


