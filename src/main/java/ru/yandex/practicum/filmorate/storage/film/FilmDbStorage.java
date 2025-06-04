package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

import static java.util.Calendar.DECEMBER;

@Slf4j
@Repository  //класс выполняет роль репозитория, то есть отвечает за хранение и извлечение данных.
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    //RowMapper<User> используется для преобразования каждой строки результата SQL-запроса в объект типа User
    private final FilmRowMapper mapper;
    private final GenreRowMapper genreRowMapper;
    private final LocalDate movieInventDate = LocalDate.of(1895, DECEMBER, 20);


    // @PostMapping
    @Override
    public Film createFilm(Film film) {
        validation(film);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery =
                "INSERT INTO films (title, description, release_date, duration, mpa_id) " +
                        "VALUES (?, ?, ?, ?, ?)";
        try {
            int createRows = jdbcTemplate.update(connection -> {
                // препарируем запрос, чтобы можно было вставить туда аргументы
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setLong(5, film.getMpa().getId());

                return stmt;
            }, keyHolder);

            if (createRows <= 0) {
                log.trace("Не удалось добавить в базу новый фильм.");
            }
        } catch (Exception e) {
            throw new ValidationException("Не удалось добавить в базу новый фильм.");
        }
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());//создали id для нового user
        //
        Set<Genre> genres = film.getGenres();

        if (genres != null)
            for (Genre genre : genres) { // проходимся по списку всех жанров в запросе
                // проверяем есть ли такой жанр в БД и если есть добавляем
                if (genre.getId() == null) continue;
                filmAddGenres(film.getId(), genre.getId());
            }
        return film;
    }

    private void filmAddGenres(Long film_id, Long genre_id) {
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)";
        try {
            int a = jdbcTemplate.update(sqlQuery, film_id, genre_id);
        } catch (Exception e) {
            throw new NotFoundException("Жанра с id " + genre_id + " не существует.");
        }
    }

    private void validation(Film film) {
        if (film.getReleaseDate().isBefore(movieInventDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        String sqlQuery = "SELECT COUNT(*) FROM mpa WHERE mpa_id = ?";
        Long a = film.getMpa().getId();
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, a);
        if (count == 0) {
            throw new NotFoundException("MPA с таким ID не существует.");
        }
        Set<Genre> gr = film.getGenres();
        if (gr != null)
            for (Genre g : gr) {
                if (g.getId() == null) continue;
                String sqlQuery2 = "SELECT COUNT(*) FROM genre WHERE genre_id = ?";

                Integer count2 = jdbcTemplate.queryForObject(sqlQuery2, Integer.class, g.getId());
                if (count2 == 0) {
                    throw new NotFoundException("жанра с таким ID не существует.");
                }
            }
    }


    //@DeleteMapping
    @Override
    public void deleteFilm(Long film_id) {
        // проверяем выполнение необходимых условий
        if (film_id <= 0) {
            throw new ValidationException("Необходимо указать id фильма, который нужно удалить.");
        }
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        //.update - выполняет запрос, то есть выполняет команду (запрос) удалить
        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать и делает.
        int deleteRow = jdbcTemplate.update(connection -> {
            // препарируем запрос, чтобы можно было вставить туда аргументы
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setLong(1, film_id); // в запрос подставляем переменную id, которая будет в строке запроса
            return stmt;
        });
        if (deleteRow <= 0) log.trace("Не удалось удалить фильм из базы данных.");

    }


    // @PutMapping
    @Override
    public Film updateFilm(Film film) {
        //validation(film);

        String sqlQuery = "UPDATE films SET title=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";
        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать и делает.
        try {
            int updatedRows = jdbcTemplate.update(connection -> {
                // препарируем запрос, чтобы можно было вставить туда аргументы
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setLong(5, film.getMpa().getId());
                stmt.setLong(6, film.getId());
                return stmt;
            });
            // Обработка ситуации, когда обновление не произошло
            if (updatedRows <= 0) {
                log.trace("Обновление не произошло");
                throw new NotFoundException("Обновление не произошло, так как новые данные совпадают со старыми.");
            }
        } catch (Exception e) {
            throw new NotFoundException("Обновление не произошло, так как новые данные совпадают со старыми.");
        }
        return film;
    }


    //GET/films/{id}
    //@GetMapping("{id}")   - выводим фильм по его id
    @Override
    public Optional<Film> getFilmByID(Long film_id) {
// проверяем выполнение необходимых условий
        if (film_id <= 0) {
            throw new ValidationException("Необходимо указать id фильма.");
        }
        log.trace("Выполнение запроса  film_id: " + film_id);
        String sqlQuery =
                "SELECT f.id, f.title, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                        "FROM films AS f " +
                        "INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                        "WHERE f.id = ? ";

        try {
            //RowMapper<Film> используется для преобразования каждой строки результата SQL-запроса в объект типа Film
            // Метод jdbcTemplate.queryForObject для вывода одного объекта, одной строки
            Film getFilm = jdbcTemplate.queryForObject(sqlQuery, mapper, film_id);

            if (getFilm == null) {
                log.trace("Не найден фильм с id: " + film_id);
                return Optional.empty();
            } else {
                log.trace("Найден фильм: " + getFilm);
                getFilm.setGenres(getGenresForFilm(film_id));

                return Optional.of(getFilm);
            }
        } catch (EmptyResultDataAccessException ignored) {
            log.trace("Не найдено результатов для film_id: " + film_id);
            return Optional.empty();
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("Ошибка при извлечении film", e);
        }
    }


    // @GetMapping
    @Override
    public List<Film> getAllFilms() {
        String sqlQuery =
                "SELECT f.id, f.title, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                        "FROM films AS f " +
                        "INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id";
        try {
            //mapper используется для преобразования каждой строки результата SQL-запроса в объект типа Films
            //метод jdbcTemplate.query - для вывода списка
            List<Film> getList = jdbcTemplate.query(sqlQuery, mapper);

            return getList;
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    // Вернуть список фильмов с определенным рейтингом MPA
    @Override
    public List<Film> getFilmsByMPA(Long mpa_id) {
        String sqlQuery =
                "SELECT f.id, f.title, f.description, f.release_date, f.duration, m.mpa_id " +
                        "FROM  films AS f " +
                        "LEFT JOIN mpa AS m ON f.mpa_id = m.id" +
                        "WHERE m.id = ?";
        if (mpa_id <= 0) {
            throw new ValidationException("Рейтинг MPA не может быть пустым.");
        }
        try {
            //RowMapper<Film> используется для преобразования каждой строки результата SQL-запроса в объект типа User
            // Метод jdbcTemplate.queryForObject для вывода одного объекта, одной строки
            List<Film> getList = jdbcTemplate.query(sqlQuery, mapper, mpa_id);
            return getList;
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private Set<Genre> getGenresForFilm(Long filmId) {
        String sqlQuery = "SELECT g.genre_id, g.genre_name " +
                "FROM film_genre AS fg " +
                "INNER JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        try {
            List<Genre> genres = jdbcTemplate.query(sqlQuery, genreRowMapper, filmId);
            if (genres == null) {
                throw new NotFoundException("нет жанров");
            }
            return new HashSet<>(genres);
        } catch (Exception e) {
            // Обработка исключений
            throw new NotFoundException("нет жанров");
        }
    }
}
