package ru.yandex.practicum.filmorate.util;

public final class ResponseClient {
    private final int cod;
    private final String body;

    public ResponseClient(int cod, String body) {
        this.cod = cod;
        this.body = body;
    }

    public int getCod() {
        return cod;
    }

    public String getBody() {
        return body;
    }
}
