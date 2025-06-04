package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

//@Component
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    //RowMapper<User> используется для преобразования каждой строки результата SQL-запроса в объект типа User
    private final UserRowMapper mapper;


    // @PostMapping - Метод добавления в таблицу записи о создании пользователя
    @Override
    public User createUser(User user) {
        validation(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //String sqlQuery = "INSERT INTO users (name, login, birthday, email) VALUES (?, ?, ?, ?) RETURNING id";
        String sqlQuery = "INSERT INTO users (name, login, birthday, email) VALUES (?, ?, ?, ?)";

        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать (INSERT INTO) и делает.
        jdbcTemplate.update(connection -> {
            // препарируем запрос, чтобы можно было вставить туда аргументы
            /*
            Метод prepareStatement(sqlQuery, new String[]{"id"}) в Java используется для создания объекта
             PreparedStatement, который представляет собой предварительно скомпилированный SQL-запрос.
             В данном случае sqlQuery — это строка с SQL-запросом, а new String[]{"id"} — массив,
             который указывает имена столбцов, возвращаемых запросом.
             */
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setDate(3, Date.valueOf(user.getBirthday()));
            stmt.setString(4, user.getEmail());
            return stmt;
        }, keyHolder);
//методу setId объекта user присваивается значение, полученное из keyHolder
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());//создали id для нового user
        return user;
    }

    //@DeleteMapping
    @Override
    public void deleteUser(Long userId) {
        // проверяем выполнение необходимых условий
        if (userId <= 0) {
            throw new ValidationException("Необходимо указать id пользователя, которого нужно удалить.");
        }
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        //.update - выполняет запрос, то есть выполняет команду (запрос) удалить
        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать и делает.
        int rez = jdbcTemplate.update(connection -> {
            // препарируем запрос, чтобы можно было вставить туда аргументы
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setLong(1, userId); // в запрос подставляем переменную id, которая будет в строке запроса
            return stmt;
        });
        if (rez <= 0) log.trace("Не удалось произвести удаление.");
    }

    // @PutMapping
    @Override
    public User updateUser(User user) {
        validation(user);

        String sqlQuery = "UPDATE users SET name=?, login=?, birthday=?, email=?  WHERE id=?";
        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать и делает.
        //Mетод update должен вернуть количество строк, затронутых операцией.
        try {
            int updatedRows = jdbcTemplate.update(connection -> {
                // препарируем запрос, чтобы можно было вставить туда аргументы
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getLogin());
                stmt.setDate(3, Date.valueOf(user.getBirthday()));
                stmt.setString(4, user.getEmail());
                stmt.setLong(5, user.getId());
                return stmt;
            });
            // Обработка ситуации, когда обновление не произошло
            if (updatedRows == 0) {
                throw new ValidationException("Обновление не произошло, так как новые данные совпадают со старыми.");
            }
        } catch (Exception e) {
            throw new NotFoundException("Обновление не произошло, так как новые данные совпадают со старыми.");
        }
        return user;
    }


    // @GetMapping
    @Override
    public Optional<User> getUser(Long id) {
// проверяем выполнение необходимых условий
        if (id <= 0) {
            throw new ValidationException("Необходимо указать id пользователя.");
        }
        String sqlQuery = "SELECT id,name, login, birthday, email FROM users WHERE id = ?";

        try {
            //RowMapper<User> используется для преобразования каждой строки результата SQL-запроса в объект типа User
            // Метод jdbcTemplate.queryForObject  для вывода одного объекта, одной строки
            User user = jdbcTemplate.queryForObject(sqlQuery, mapper, id);
            if (user == null) return Optional.empty();
            return Optional.of(user);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }


    // @GetMapping
    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT id, name, login, birthday, email FROM users";
        try {
            //RowMapper<User> используется для преобразования каждой строки результата SQL-запроса в объект типа User
            //метод jdbcTemplate.query  - для вывода списка
            List<User> a = jdbcTemplate.query(sqlQuery, mapper);
            return a;
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String sqlQuery = "SELECT id, name, login, birthday, email  FROM users WHERE email = ?";
        if (email.isBlank() || email.isEmpty()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        try {
            //RowMapper<User> используется для преобразования каждой строки результата SQL-запроса в объект типа User
            // Метод jdbcTemplate.queryForObject  для вывода одного объекта, одной строки
            User a = jdbcTemplate.queryForObject(sqlQuery, mapper, email);
            return a;
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public Set<Long> getAllIdUsers() {
        String sqlQuery = "SELECT id FROM users";
        /*
        -Метод jdbcTemplate.query используется для выполнения этого запроса sqlQuery.
        -В качестве второго параметра передаётся анонимный класс, реализующий интерфейс ResultSetExtractor,
        который обрабатывает результаты запроса.
        -Внутри ResultSetExtractor создаётся пустой набор ids типа Set<Long> для хранения идентификаторов
        -С помощью цикла while (rs.next()) перебираются все строки результата запроса.
        - Для каждой строки идентификатор (id) извлекается методом rs.getLong("id") и добавляется в набор ids
        -После обработки всех строк результат (набор ids) возвращается из метода.
         */

        Set<Long> a = jdbcTemplate.query(sqlQuery, (ResultSetExtractor<Set<Long>>) rs -> {
            Set<Long> ids = new HashSet<>();
            while (rs.next()) {
                ids.add(rs.getLong("id"));
            }
            return ids;
        });
        return a;
    }


    protected void validation(User user) {
        // проверяем выполнение необходимых условий
        if (user.getLogin().isBlank() || user.getLogin().matches(".*\\s.*")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
