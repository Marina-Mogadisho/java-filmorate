package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 RowMapper для преобразования записей из таблицы
 в объекты типа Genre
 */
@Component
public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getLong("genre_id"));
        genre.setName(resultSet.getString("genre_name"));
        return genre;
    }
}


/*
Мы реализовали класс для преобразования записей таблицы в объекты типа — Genre. Единственный метод mapRow преобразует
данные из каждой ячейки в Java-сущности. Для этого используются стандартные методы класса ResultSet.
 Они определяют соответствие типов данных из БД типам Java.
 */
