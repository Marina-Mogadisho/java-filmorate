package ru.yandex.practicum.filmorate.model;

import lombok.Data;

//СВязь: один фильм ко многим (лайкам)
@Data
public class LikeFilm {
    Long filmId;
    Long userId;
}
