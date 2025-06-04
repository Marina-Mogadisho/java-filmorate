package ru.yandex.practicum.filmorate.model;

public class Friendship {
    /*
    user_id будет содержать ID пользователя, который добавил друга.
    friend_id будет содержать ID пользователя, которого добавили в друзья.
    Дружба всегда подтверждена только у первого пользователя user_id
    Второй если подтвердит, то будет еще одна строка, гдк уже он будет первым user_id
     */

    Long user_id;  //
    Long friend_id; //
}
