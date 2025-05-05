package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//будет отвечать за операции с фильмами —
// добавление и удаление лайка,
// вывод 10 наиболее популярных фильмов по количеству лайков.
@Service //к ним можно будет получить доступ из контроллера.
public class FilmService {

    UserStorage userStorage;
    FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    public Film addLikeToFilm(Long idFilm, Long idUser) {
        validation(idFilm, idUser);
        Film film = filmStorage.getFilm(idFilm);
        User user = userStorage.getUser(idUser);
        // добавили id пользователя в список пользователей, которые поставили лайки этому фильму
        film.getLikeUser().add(idUser);
        // Добавили id фильма в список фильмов, которым пользователь поставил лайк
        user.getFilm().add(idFilm);
        return filmStorage.addLike(idFilm); // увеличили количество лайков фильма на 1 лайк
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
        return filmStorage.deleteLike(idFilm);// уменьшили количество лайков фильма на 1 лайк
    }

    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10
    public List<Film> getListFilmsPopular(Integer count) {
        if (count == null) count = 10;
        List<Film> listFilms = new ArrayList<>();
        ArrayList<Film> listSort = new ArrayList<>(filmStorage.findAll()); // достали список фильмов
        listSort.sort(new CompareFilm()); // Отсортировали фильмы в списке по большинству лайков
        if (count > 0) {
            if (count <= listSort.size()) {
                listFilms = listSort.subList(0, count);
            } else listFilms = listSort.subList(0, listSort.size());
        } else {
            throw new RuntimeException("error arg");
        }
        return listFilms;
    }

    //Вспомогательный метод, проверяем существуют ли пользователи с указанными в запросе id
    private void validation(Long idFilm, Long idUser) {
        Set<Long> setIdUsers = userStorage.getAllIdUsers();
        Set<Long> setIdFilm = filmStorage.getAllIdFilms();

        if (!setIdFilm.contains(idFilm)) {
            throw new NotFoundException("Фильм с id = " + idFilm + " не найден.");
        }
        if (!setIdUsers.contains(idUser)) {
            throw new NotFoundException("Пользователь с id =  " + idUser + " не найден.");
        }
    }
}
