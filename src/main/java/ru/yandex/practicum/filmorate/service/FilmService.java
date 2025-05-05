package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

//будет отвечать за операции с фильмами —
// добавление и удаление лайка,
// вывод 10 наиболее популярных фильмов по количеству лайков.
@Service //к ним можно будет получить доступ из контроллера.
@RequiredArgsConstructor
public class FilmService {

    final UserStorage userStorage;
    final FilmStorage filmStorage;

    //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    public Film addLikeToFilm(Long idFilm, Long idUser) {
        validation(idFilm, idUser);
        Film film = filmStorage.getFilm(idFilm);
        User user = userStorage.getUser(idUser);
        // добавили id пользователя в список пользователей, которые поставили лайки этому фильму
        film.getLikeUser().add(idUser);
        // Добавили id фильма в список фильмов, которым пользователь поставил лайк
        user.getFilm().add(idFilm);
        return film; // увеличили количество лайков фильма на 1 лайк
    }

    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    public Film deleteLikeToFilm(Long idFilm, Long idUser) {
        validation(idFilm, idUser);
        Film film = filmStorage.getFilm(idFilm);
        User user = userStorage.getUser(idUser);
        //удаляем id пользователя из списка пользователей, которые поставили лайки этому фильму
        film.getLikeUser().remove(idUser);
        // удаляем id фильма из списка фильмов, которым пользователь поставил лайк
        user.getFilm().remove(idFilm);
        return film;// уменьшили количество лайков фильма на 1 лайк
    }

    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10
    public List<Film> getListFilmsPopular(Integer count) {
        List<Film> listFilms;
        List<Film> listSort = filmStorage.findAll();
        listSort.sort(Comparator.comparing(Film::getLike).reversed());
        if (count <= listSort.size()) {
            listFilms = listSort.subList(0, count);
        } else listFilms = listSort.subList(0, listSort.size());
        return listFilms;
    }

    //Вспомогательный метод, проверяем существуют ли пользователи с указанными в запросе id
    private void validation(Long idFilm, Long idUser) {
        User user = userStorage.getUser(idUser);
        Film film = filmStorage.getFilm(idFilm);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + idFilm + " не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id =  " + idUser + " не найден.");
        }
    }
}
