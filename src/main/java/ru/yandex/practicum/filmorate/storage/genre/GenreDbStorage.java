package ru.yandex.practicum.filmorate.storage.genre;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository  //класс выполняет роль репозитория, то есть отвечает за хранение и извлечение данных.
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper mapper;


    @Override
    public List<Genre> getListGenre(int id_film) {
        String sqlQuery =
                "SELECT genre_name" +
                        "FROM film_genre" +
                        "JOIN genre ON film_genre.genre_id = genre.genre_id" +
                        "WHERE film_id = ?";
        try {
            return jdbcTemplate.query(sqlQuery, mapper, id_film);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public Genre getGenre(int id_genre) {
        String sqlQuery = "SELECT id AS genre_id, name AS genre_name FROM genre WHERE id=?";
        try {
            List<Genre> list = jdbcTemplate.query(sqlQuery, mapper, id_genre);
            return list.get(0);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }
}
