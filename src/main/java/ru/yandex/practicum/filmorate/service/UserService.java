package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

//будет отвечать за такие операции с пользователями как:
// добавление в друзья,
// удаление из друзей,
// вывод списка общих друзей.
@Service //к ним можно будет получить доступ из контроллера.
public class UserService {
    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    //Метод добавления Friend в список друзей пользователя user
    // PUT /users/{id}/friends/{friendId}
    public User addFriends(Long idUser, Long idFriend) {
        validation(idFriend, idUser); // проверяем существуют ли такие пользователи
        User user = inMemoryUserStorage.getUser(idUser);
        if (user.getFriends().add(idFriend)) {
            throw new ValidationException("Друг с id " + idFriend + " уже есть в списке друзей пользователя User");
        }
        User newFriend = inMemoryUserStorage.getUser(idFriend);
        if (newFriend.getFriends().add(idUser)) {
            throw new ValidationException("Друг с таким id уже есть в списке друзей пользователя Friend");
        }
        return user;
    }

    // Метод удаления друга Friend из списка друзей пользователя User
    //DELETE /users/{id}/friends/{friendId}
    public User deleteFriend(Long idUser, Long idFriend) {
        validation(idFriend, idUser); // проверяем существуют ли такие пользователи

        User user = inMemoryUserStorage.getUser(idUser);
        if (user.getFriends().remove(idFriend)) {
            throw new ValidationException("Удаление невозможно. " +
                    "Друга с id " + idFriend + " нет в списке друзей пользователя User");
        }

        User newFriend = inMemoryUserStorage.getUser(idFriend);
        if (newFriend.getFriends().remove(idUser)) {
            throw new ValidationException("Удаление невозможно. " +
                    "У пользователя Friend нет друга User с id " + idUser);
        }
        return user;
    }


    // Метод — возвращаем список друзей пользователя User
    //GET /users/{id}/friends
    public List<User> getAllFriends(Long idUser) {
        if (inMemoryUserStorage.getAllIdUsers().contains(idUser)) {
            throw new NotFoundException("Пользователь с id =  " + idUser + " не найден.");
        }
        ArrayList<User> listFriends = new ArrayList<>();
        User user = inMemoryUserStorage.getUser(idUser);
        Set<Long> friends = user.getFriends();
        for (Long id : friends) {
            User userFriend = inMemoryUserStorage.getUser(id);
            listFriends.add(userFriend);
        }
        return listFriends;
    }

    //Метод — возвращает список друзей, общих с другим пользователем.
    //GET /users/{id}/friends/common/{otherId}
    public List<User> getListFriendsTwoUsers(Long idUser1, Long idUser2) {
        ArrayList<User> sharedListFriends = new ArrayList<>();
        validation(idUser1, idUser2);
        User user1 = inMemoryUserStorage.getUser(idUser1);
        User user2 = inMemoryUserStorage.getUser(idUser2);

        Set<Long> setIDFriendsUser1 = user1.getFriends();
        Set<Long> setIDFriendsUser2 = user2.getFriends();

        // метод retainAll сохраняет в списке setIDFriendsUser1 только те элементы,
        // которые совпадают с элементами списка setIDFriendsUser2
        setIDFriendsUser1.retainAll(setIDFriendsUser2);
        for (Long id : setIDFriendsUser1) {
            User userFriend = inMemoryUserStorage.getUser(id);
            sharedListFriends.add(userFriend);
        }
        return sharedListFriends;
    }

    //Вспомогательный метод, проверяем существуют ли пользователи с указанными в запросе id
    private void validation(Long idFriend, Long idUser) {
        Set<Long> setIdUsers = inMemoryUserStorage.getAllIdUsers();
        if (setIdUsers.contains(idFriend)) {
            throw new NotFoundException("Пользователь с id = " + idFriend + " не найден.");
        }
        if (setIdUsers.contains(idUser)) {
            throw new NotFoundException("Пользователь с id =  " + idUser + " не найден.");
        }
    }
}
