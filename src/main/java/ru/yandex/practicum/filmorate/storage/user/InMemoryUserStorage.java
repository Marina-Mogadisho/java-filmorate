package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

//вся логика хранения, обновления и поиска объектов.
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Set<Long> getAllIdUsers() {
        return users.keySet();
    }

    public User createUser(User user) {
        validation(user);
        user.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User newUser) {
        // проверяем необходимые условия
        //log.warn("Id должен быть указан");
        if (newUser.getId() == null) {
            throw new NotFoundException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Не могу обновить, такого Id пользователя не существует.");
        }

        validation(newUser);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    public void deleteUser(User user) {

    }

    public User getUser(Long id) {
        return users.get(id);
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void validation(User user) {
        // проверяем выполнение необходимых условий
        if (user.getLogin().isBlank() || user.getLogin().matches(".*\\s.*")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
