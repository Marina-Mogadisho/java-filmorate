package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 RowMapper для преобразования записей из таблицы
 в объекты типа Film:
 */
@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("title"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        MPA mpa = new MPA(resultSet.getLong("mpa_id"), resultSet.getString("mpa_name"));
        film.setMpa(mpa);
        return film;
    }
}

    /*
Мы реализовали класс для преобразования записей таблицы films в объекты типа — Film. Единственный метод mapRow преобразует
данные из каждой ячейки в Java-сущности. Для этого используются стандартные методы класса ResultSet.
 Они определяют соответствие типов данных из БД типам Java.
 */

