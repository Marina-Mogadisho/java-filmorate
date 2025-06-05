package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
Класс UserDbService будет отвечать за такие операции с пользователями как:
добавление в друзья,
удаление из друзей,
вывод списка общих друзей
Подтверждение дружбы
 */

@Slf4j
@Service //к ним можно будет получить доступ из контроллера.
@RequiredArgsConstructor // то же самое, если использовать конструктор с @Autowired
public class UserDbService {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;
    /*
    user отправляет заявку в друзья пользователю friend и тем самым
    он добавляет другого человека в свой список друзей, но сам в его список не попадает.
    */

    //добавление записи о дружбе:
    // PUT /users/{id}/friends/{friendId}
    public Optional<User> addFriends(Long userId, Long friendId) {

        validation(userId, friendId); // проверяем существуют ли такие пользователи
        Optional<User> user = userDbStorage.getUser(userId);
        Set<Long> b = user.get().getFriends(); // Достали список id друзей типа Set, т.е. без повторений

        // Если такого id нет в списке, то он добавился в список друзей, и переменная а=true
        if (isFriends(userId, friendId, false)) { // Для проверки наличия дружбы между пользователями,
            throw new ValidationException("Пользователь с id " + friendId + " уже есть в списке друзей пользователя User");
        }

        //Добавили запись в БД
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        int a = jdbcTemplate.update(sql, userId, friendId);
        if (a <= 0) throw new ValidationException("Запись о дружбе не добавилась.");
        return user;
    }


    // Метод удаления друга Friend из списка друзей пользователя User
//DELETE /users/{id}/friends/{friendId}
    public Optional<User> deleteFriend(Long userId, Long friendId) {
        validation(userId, friendId); // проверяем существуют ли такие пользователи
        //проверяем являются ли пользователи друзьями
        if (!isFriends(userId, friendId, true)) {
            throw new ValidationException("Пользователь с id " + friendId +
                    " не в списке друзей пользователя с id " + userId);
        }

        // 1 ШАГ : Удаляем строку из Таблицы friendship Базы данных
        String sqlQuery = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        //.update - выполняет запрос, то есть выполняет команду (запрос) удалить
        // jdbcTemplate.update как бы читает в запросе команду, что нужно делать и делает.
        int updatedRows = jdbcTemplate.update(connection -> {
            // препарируем запрос, чтобы можно было вставить туда аргументы
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setLong(1, userId); // в запрос подставляем переменную id, которая будет в строке запроса
            stmt.setLong(2, friendId); // в запрос подставляем переменную id, которая будет в строке запроса
            return stmt;
        });
        // Обработка ситуации, когда обновление не произошло
        if (updatedRows < 0) {
            throw new ValidationException("Удаление не произошло.");
        }

        //2 ШАГ : Удаляем из Set<Long> friends пользователя с id userId
        Optional<User> user = userDbStorage.getUser(userId);
        if (!user.get().getFriends().remove(friendId)) {
            log.trace("Удаление невозможно. " +
                    "Друга с id " + friendId + " нет в списке друзей пользователя User " + userId);
        }
        return user;
    }


    // Метод — возвращаем список друзей пользователя User
//GET /users/{id}/friends
    public List<User> getAllFriends(Long userId) {
        if (userId <= 0) throw new ValidationException("id должно быть положительным числом и больше 0.");
        String sqlQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId);
        if (count == 0) {
            throw new NotFoundException("Пользователь с таким ID не существует.");
        }
        String sqlQuery2 =
                "SELECT u.id, u.name AS name_friend " +
                        "FROM users AS u " +
                        "INNER JOIN friendships AS fs ON u.id = fs.friend_id " +
                        "WHERE fs.user_id = ?";
        List<User> a = jdbcTemplate.query(sqlQuery2, new Object[]{userId}, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name_friend"));

            return user;
        });
        return a;
    }


    //Метод — возвращает список друзей, общих с другим пользователем.
//GET /users/{id}/friends/common/{otherId}
    public List<User> getListFriendsTwoUsers(Long idUser1, Long idUser2) {

        String sqlQuery =
                "SELECT u.id, u.name " +
                        "FROM friendships AS f1 " +
                        "JOIN friendships AS f2 ON f1.friend_id = f2.friend_id " +
                        "JOIN users u ON f1.friend_id = u.id " +
                        "WHERE f1.user_id = ? " +
                        "AND f2.user_id = ?;";


        List<User> a = jdbcTemplate.query(sqlQuery, new Object[]{idUser1, idUser2}, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            return user;
        });
        return a;
    }

    //Вспомогательный метод, проверяем являются ли пользователи друзьями
    // метод, который проверяет наличие записи в таблице Friendships:
    //Для проверки наличия дружбы между пользователями,
    public boolean isFriends(Long userId, Long friendId, boolean isUseNull) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        // isUseNull=true, когда нужно использовать в методе
        if (isUseNull) return count >= 0;
        else return count > 0;
    }

    //Вспомогательный метод, проверяем существуют ли пользователи с указанными в запросе id
    private void validation(Long userId, Long friendId) {
        Set<Long> setIdUsers = userDbStorage.getAllIdUsers();
        if (userId.equals(friendId)) {
            throw new ValidationException("ID пользователей не могут быть одинаковыми.");
        }
        if (!setIdUsers.contains(friendId)) {
            throw new NotFoundException("Пользователь Friend с id = " + friendId + " не найден в Базе данных.");
        }
        if (!setIdUsers.contains(userId)) {
            throw new NotFoundException("Пользователь User с id =  " + userId + " не найден в Базе данных.");
        }
    }
}
