package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

//будет отвечать за операции с фильмами —
// добавление и удаление лайка,
// вывод 10 наиболее популярных фильмов по количеству лайков.


@Slf4j
@Service //к ним можно будет получить доступ из контроллера.
@RequiredArgsConstructor
public class FilmDbService {
    private final UserDbStorage userDBStorage;
    private final FilmDbStorage filmDBStorage;
    private final FilmRowMapper filmRowMapper;
    private final MpaRowMapper mpaRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final JdbcTemplate jdbcTemplate;


    //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    public Optional<Film> addLikeToFilm(Long film_id, Long user_id) {
        validation(user_id, film_id);
        Optional<Film> film = filmDBStorage.getFilmByID(film_id);
        Optional<User> user = userDBStorage.getUser(user_id);

        String sql = "INSERT INTO like_film (user_id, film_id) VALUES (?, ?)";
        int createRows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, user_id);
            stmt.setLong(2, film_id);
            return stmt;
        });
        if (createRows <= 0) {
            log.trace("Не удалось добавить в базу like_film новую связь о дружбе.");
            throw new ValidationException("Не удалось добавить в базу like_film новую связь о дружбе.");
        }
        // добавили id пользователя в список пользователей, которые поставили лайки этому фильму
        film.get().getLikeUser().add(user_id);
        // Добавили id фильма в список фильмов, которым пользователь поставил лайк
        user.get().getFilm().add(film_id);
        return film; // увеличили количество лайков фильма на 1 лайк
    }


    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    public Optional<Film> deleteLikeToFilm(Long film_id, Long user_id) {
        validation(user_id, film_id);

        //Удаляем строку из Таблицы like_film Базы данных
        String sqlQuery = "DELETE FROM like_film WHERE user_id = ? and film_id = ?";
        //.update - выполняет запрос, то есть выполняет команду (запрос) удалить
        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать и делает.
        try {
            int updatedRows = jdbcTemplate.update(connection -> {
                // препарируем запрос, чтобы можно было вставить туда аргументы
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setLong(1, user_id); // в запрос подставляем переменную id, которая будет в строке запроса
                stmt.setLong(2, film_id); // в запрос подставляем переменную id, которая будет в строке запроса
                return stmt;
            });
            // Обработка ситуации, когда обновление не произошло
            if (updatedRows == 0) {
                throw new ValidationException("Удаление не произошло.");
            }
        } catch (Exception e) {
            throw new NotFoundException("ex:" + e);
        }
        return filmDBStorage.getFilmByID(film_id);// уменьшили количество лайков фильма на 1 лайк
    }


    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10
    public List<Film> getListFilmsPopular(Integer count) {
        if (count == null || count <= 0) count = 10;

        String sqlQuery =
                "select f.id, f.title, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name, ff.like_count" +
                        " FROM films AS f,mpa AS m, " +
                        " (SELECT lf.film_id as f_id, COUNT(lf.user_id) AS like_count " +
                        " from like_film as lf " +
                        " GROUP BY lf.film_id " +
                        " ORDER BY like_count DESC " +
                        " LIMIT ? " +
                        " ) as ff" +
                        " where f.mpa_id = m.mpa_id " +
                        " and f.id =ff.f_id" +
                        " order by ff.like_count desc ";

        List<Film> list_films;
        try {
            list_films = jdbcTemplate.query(sqlQuery, filmRowMapper, count);
        } catch (Exception e) {
            throw new NotFoundException("ERROR !!!!!!!!!!!\n" + e);
        }
        return list_films;
    }


    //Вспомогательный метод, проверяем существуют ли пользователи с указанными в запросе id
    private void validation(Long user_id, Long film_id) {
        Optional<User> user = userDBStorage.getUser(user_id);
        Optional<Film> film = filmDBStorage.getFilmByID(film_id);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + film_id + " не найден.");
        }
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id =  " + user_id + " не найден.");
        }
    }


    public Optional<MPA> getMpa(Long mpaId) {
        String sqlQuery =
                "SELECT mpa_id, mpa_name " +
                        "FROM mpa WHERE mpa_id=? ";
        try {
            MPA m = jdbcTemplate.queryForObject(sqlQuery, mpaRowMapper, mpaId);
            if (m == null) throw new NotFoundException("Нет такого MPA");
            return Optional.of(m);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("ex:" + e);
        }
    }


    public List<MPA> getAllMpas() {
        String sqlQuery = "SELECT mpa_id, mpa_name FROM mpa order by mpa_id";
        try {
            //метод jdbcTemplate.query - для вывода списка
            List<MPA> getList = jdbcTemplate.query(sqlQuery, mpaRowMapper);
            return getList;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("ex:" + e);
        }
    }


    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT genre_id, genre_name FROM genre order by genre_id";
        try {
            //метод jdbcTemplate.query - для вывода списка
            return jdbcTemplate.query(sqlQuery, genreRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("ex:" + e);
        }
    }


    //Вывести данные из таблицы жанров
    public Optional<Genre> getGenreById(Long genreId) {
        String sqlQuery =
                "SELECT genre_id, genre_name FROM genre WHERE genre_id=? ";
        try {
            Genre g = jdbcTemplate.queryForObject(sqlQuery, genreRowMapper, genreId);
            if (g == null) throw new NotFoundException("Нет такого Genre");
            return Optional.of(g);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("ex:" + e);
        }
    }
}


