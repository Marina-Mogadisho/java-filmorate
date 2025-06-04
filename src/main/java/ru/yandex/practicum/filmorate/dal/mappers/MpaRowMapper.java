package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 RowMapper для преобразования записей из таблицы
 в объекты типа Genre
 */
@Component
public class MpaRowMapper implements RowMapper<MPA> {
    @Override
    public MPA mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        MPA mpa = new MPA();
        mpa.setId(resultSet.getLong("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        return mpa;
    }
}


/*
Мы реализовали класс для преобразования записей таблицы в объекты типа — Genre. Единственный метод mapRow преобразует
данные из каждой ячейки в Java-сущности. Для этого используются стандартные методы класса ResultSet.
 Они определяют соответствие типов данных из БД типам Java.
 */
