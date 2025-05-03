package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    //класс зависимость
    @Autowired
    UserService userService;
    @Autowired
    InMemoryUserStorage inMemoryUserStorage;

    //GET /users — для получения списка пользователей.
    @GetMapping  //метод обрабатывает HTTP-запросы GET с корневым путем  /users
    public List<User> findAll() {
        return inMemoryUserStorage.getAllUsers();
    }

    //POST /users — для добавления нового пользователя в список.
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        return inMemoryUserStorage.updateUser(newUser);
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping
    public User addFriends(Long idUser, Long idFriend) {
        return userService.addFriends(idUser, idFriend);
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping
    public User deleteFriends (Long idUser, Long idFriend){
        return userService.deleteFriend(idUser,idFriend);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping
    public List<User> getAllFriends(Long idUser) {
        return userService.getAllFriends(idUser);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping
    public List<User> getListFriendsTwoUsers(Long idUser, Long idFriend){
        return userService.getListFriendsTwoUsers(idUser, idFriend);
    }
}
