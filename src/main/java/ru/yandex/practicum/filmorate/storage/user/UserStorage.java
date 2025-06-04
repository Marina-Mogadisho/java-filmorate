package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//в интерфейсе
// определены методы добавления, удаления и модификации объектов.
//@Deprecated
public interface UserStorage {

    public List<User> getAllUsers();

    public User updateUser(User newUser);

    public Optional<User> getUser(Long id);

    public User getUserByEmail(String email);

    public User createUser(User user);

    public void deleteUser(Long user_id);

    public Set<Long> getAllIdUsers();
}
