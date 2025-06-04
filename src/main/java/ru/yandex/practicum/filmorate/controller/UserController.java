package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;
    UserStorage userStorage;

    @Autowired
    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }


    //GET /users — для получения списка пользователей.
    @GetMapping  //метод обрабатывает HTTP-запросы GET с корневым путем  /users
    public List<User> findAll() {
        return userStorage.getAllUsers();
    }

    //POST /users — для добавления нового пользователя в список.
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        return userStorage.updateUser(newUser);
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriends(id, friendId);
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        User u = userService.deleteFriend(id, friendId);
        return u;
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        return userService.getAllFriends(id);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListFriendsTwoUsers(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getListFriendsTwoUsers(id, otherId);
    }
}
