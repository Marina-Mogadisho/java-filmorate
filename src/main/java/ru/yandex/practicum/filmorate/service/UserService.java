package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//будет отвечать за такие операции с пользователями как:

// добавление в друзья,
// удаление из друзей,
// вывод списка общих друзей.
@Slf4j
@Service //к ним можно будет получить доступ из контроллера.
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;

    }

    //Метод добавления Friend в список друзей пользователя user
    // PUT /users/{id}/friends/{friendId}
    public User addFriends(Long idUser, Long idFriend) {
        validation(idFriend, idUser); // проверяем существуют ли такие пользователи
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        Set<Long> b = user.getFriends(); // Достали список id друзей типа Set, т.е. без повторений
        boolean a = b.add(idFriend); // Если такого id нет в списке, то он добавился в список друзей, и переменная а=true
        if (!a) {
            throw new ValidationException("Друг с id " + idFriend + " уже есть в списке друзей пользователя User");
        }
        User newFriend = userStorage.getUser(idFriend);
        if (!newFriend.getFriends().add(idUser)) {
            throw new ValidationException("Друг с id " + idUser + "уже есть в списке друзей пользователя Friend");
        }
        return user;
    }

    // Метод удаления друга Friend из списка друзей пользователя User
    //DELETE /users/{id}/friends/{friendId}
    public User deleteFriend(Long idUser, Long idFriend) {
        validation(idFriend, idUser); // проверяем существуют ли такие пользователи

        User user = userStorage.getUser(idUser);
        if (!user.getFriends().remove(idFriend)) {
            log.trace("Удаление невозможно. " +
                    "Друга с id " + idFriend + " нет в списке друзей пользователя User " + idUser);
        }
        User newFriend = userStorage.getUser(idFriend);

        if (!newFriend.getFriends().remove(idUser)) {
            log.trace("Удаление невозможно. " +
                    "У пользователя Friend " + idFriend + " нет друга User с id " + idUser);
        }
        return user;
    }

    // Метод — возвращаем список друзей пользователя User
    //GET /users/{id}/friends
    public List<User> getAllFriends(Long idUser) {
        if (!userStorage.getAllIdUsers().contains(idUser)) {
            throw new NotFoundException("Пользователь с id =  " + idUser + " не найден.");
        }
        ArrayList<User> listFriends = new ArrayList<>();
        User user = userStorage.getUser(idUser);
        Set<Long> friends = user.getFriends();
        for (Long id : friends) {
            User userFriend = userStorage.getUser(id);
            listFriends.add(userFriend);
        }
        return listFriends;
    }

    //Метод — возвращает список друзей, общих с другим пользователем.
    //GET /users/{id}/friends/common/{otherId}
    public List<User> getListFriendsTwoUsers(Long idUser1, Long idUser2) {
        ArrayList<User> sharedListFriends = new ArrayList<>();
        validation(idUser1, idUser2);
        User user1 = userStorage.getUser(idUser1);
        User user2 = userStorage.getUser(idUser2);

        Set<Long> setIDFriendsUser1 = user1.getFriends();
        Set<Long> setIDFriendsUser2 = user2.getFriends();

        // метод retainAll сохраняет в списке setIDFriendsUser1 только те элементы,
        // которые совпадают с элементами списка setIDFriendsUser2
        setIDFriendsUser1.retainAll(setIDFriendsUser2);
        for (Long id : setIDFriendsUser1) {
            User userFriend = userStorage.getUser(id);
            sharedListFriends.add(userFriend);
        }
        return sharedListFriends;
    }

    //Вспомогательный метод, проверяем существуют ли пользователи с указанными в запросе id
    private void validation(Long idFriend, Long idUser) {
        Set<Long> setIdUsers = userStorage.getAllIdUsers();
        if (!setIdUsers.contains(idFriend)) {
            throw new NotFoundException("Пользователь Friend с id = " + idFriend + " не найден.");
        }
        if (!setIdUsers.contains(idUser)) {
            throw new NotFoundException("Пользователь User с id =  " + idUser + " не найден.");
        }
    }
}
