package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserDbService userDbService;
    private final UserDbStorage userDbStorage;

    //GET /users — для получения списка пользователей.
    @GetMapping  //метод обрабатывает HTTP-запросы GET с корневым путем  /users
    public List<User> findAll() {
        return userDbStorage.getAllUsers();
    }

    //POST /users — для добавления нового пользователя в список.
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userDbStorage.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        return userDbStorage.updateUser(newUser);
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public Optional<User> addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        return userDbService.addFriends(id, friendId);
    }


    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public Optional<User> deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        return userDbService.deleteFriend(id, friendId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        return userDbService.getAllFriends(id);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListFriendsTwoUsers(@PathVariable Long id, @PathVariable Long otherId) {
        return userDbService.getListFriendsTwoUsers(id, otherId);
    }
}
