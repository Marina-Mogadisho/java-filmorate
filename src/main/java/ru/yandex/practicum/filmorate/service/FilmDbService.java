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
    public Optional<Film> addLikeToFilm(Long filmId, Long userId) {
        validation(userId, filmId);
        Optional<Film> film = filmDBStorage.getFilmByID(filmId);
        Optional<User> user = userDBStorage.getUser(userId);

        String sql = "INSERT INTO like_film (user_id, film_id) VALUES (?, ?)";
        int createRows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, userId);
            stmt.setLong(2, filmId);
            return stmt;
        });
        if (createRows <= 0) {
            log.trace("Не удалось добавить в базу like_film новую связь о дружбе.");
            throw new ValidationException("Не удалось добавить в базу like_film новую связь о дружбе.");
        }
        // добавили id пользователя в список пользователей, которые поставили лайки этому фильму
        film.get().getLikeUser().add(userId);
        // Добавили id фильма в список фильмов, которым пользователь поставил лайк
        user.get().getFilm().add(filmId);
        return film; // увеличили количество лайков фильма на 1 лайк
    }


    //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    public Optional<Film> deleteLikeToFilm(Long filmId, Long userId) {
        validation(userId, filmId);

        //Удаляем строку из Таблицы like_film Базы данных
        String sqlQuery = "DELETE FROM like_film WHERE user_id = ? and film_id = ?";
        //.update - выполняет запрос, то есть выполняет команду (запрос) удалить
        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать и делает.
        try {
            int updatedRows = jdbcTemplate.update(connection -> {
                // препарируем запрос, чтобы можно было вставить туда аргументы
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setLong(1, userId); // в запрос подставляем переменную id, которая будет в строке запроса
                stmt.setLong(2, filmId); // в запрос подставляем переменную id, которая будет в строке запроса
                return stmt;
            });
            // Обработка ситуации, когда обновление не произошло
            if (updatedRows == 0) {
                throw new ValidationException("Удаление не произошло.");
            }
        } catch (Exception e) {
            throw new NotFoundException("ex:" + e);
        }
        return filmDBStorage.getFilmByID(filmId);// уменьшили количество лайков фильма на 1 лайк
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

        List<Film> filmList;
        try {
            filmList = jdbcTemplate.query(sqlQuery, filmRowMapper, count);
        } catch (Exception e) {
            throw new NotFoundException("ERROR !!!!!!!!!!!\n" + e);
        }
        return filmList;
    }


    //Вспомогательный метод, проверяем существуют ли пользователи с указанными в запросе id
    private void validation(Long userId, Long filmId) {
        Optional<User> user = userDBStorage.getUser(userId);
        Optional<Film> film = filmDBStorage.getFilmByID(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id =  " + userId + " не найден.");
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


