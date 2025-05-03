package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;

//@Component  аннотация, которая определяет класс как управляемый
// Spring. Такой класс будет
// добавлен в контекст приложения при сканировании.

//вся логика хранения, обновления и поиска объектов.
@Component
public class InMemoryFilmStorage implements FilmStorage {
}
