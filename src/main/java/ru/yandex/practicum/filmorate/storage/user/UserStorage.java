package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

//в интерфейсе
// определены методы добавления, удаления и модификации объектов.
public interface UserStorage {

    public List<User> getAllUsers();

    public User createUser(User user);

    public User updateUser(User newUser);

    public void deleteUser(User user);

    public User getUser (Long id);

    public Set<Long> getAllIdUsers();

}
