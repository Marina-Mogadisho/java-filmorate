package ru.yandex.practicum.filmorate.model;

import lombok.Data;

//СВязь: один фильм ко многим (лайкам)
@Data
public class likeFilm {
    Long film_id;
    Long user_id;
}
