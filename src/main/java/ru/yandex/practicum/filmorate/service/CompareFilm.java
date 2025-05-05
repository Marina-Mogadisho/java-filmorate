package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class CompareFilm implements Comparator<Film> {

    @Override
    public int compare(Film o1, Film o2) {
        return o2.getLike().compareTo(o1.getLike());
    }
}