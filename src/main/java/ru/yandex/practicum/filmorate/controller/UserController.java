package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    //GET /users — для получения списка пользователей.
    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    //POST /users — для добавления нового пользователя в список.
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        validation(user);
        user.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        // проверяем необходимые условия
        log.warn("Id должен быть указан");
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            throw new ValidationException("Не могу обновить, такого Id пользователя не существует.");
        }

        validation(newUser);
        users.put(newUser.getId(), newUser);
        return newUser;
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

    private void validation(User user) {
        // проверяем выполнение необходимых условий
        if (user.getLogin().matches(".*\\s.*")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
