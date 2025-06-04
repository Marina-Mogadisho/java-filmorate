package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;

/*
 RowMapper для преобразования записей из таблицы
 в объекты типа User:
 */

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setLogin(resultSet.getString("login"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        user.setEmail(resultSet.getString("email"));
        return user;
    }
}


/*
Мы реализовали класс для преобразования записей таблицы films в объекты типа — Film. Единственный метод mapRow преобразует
данные из каждой ячейки в Java-сущности. Для этого используются стандартные методы класса ResultSet.
 Они определяют соответствие типов данных из БД типам Java.
 */
