package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    String error;// название ошибки

    public ErrorResponse(String error) {
        this.error = error;
    }
}