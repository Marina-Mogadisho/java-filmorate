package ru.yandex.practicum.filmorate.model;

import lombok.Data;

//СВязь: один фильм ко многим (лайкам)
@Data
public class Like_Film {
    Long film_id;
    Long user_id;
}
