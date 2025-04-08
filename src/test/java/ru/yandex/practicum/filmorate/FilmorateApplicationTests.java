package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.util.ResponseClient;
import ru.yandex.practicum.filmorate.util.UtilHttp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmorateApplicationTests {

    @BeforeAll
    static void start() {
        String[] args = new String[0];
        SpringApplication.run(FilmorateApplication.class, args);
        System.out.println("START ========================================================");
    }

    @AfterAll
    static void stop() {
        System.out.println("STOP ========================================================");
        //System.exit(0);
    }

    @Test
    void contextLoads() {
        ResponseClient response1 = UtilHttp.send("POST",
                "http://localhost:8080/users",
                "{\"login\": \"dolore\",\"name\": \"NickName\",\"email\": \"mail@mail.ru\",\"birthday\": \"1986-08-20\"}");
        System.out.println("create user ret:" + response1.getCod());
        System.out.println("create user ret:" + response1.getBody());
        ResponseClient response2 = UtilHttp.send("GET", "http://localhost:8080/users", "");
        System.out.println("Users:" + response2.getBody());
    }

    @Test
    void userControllerPOSTTest() {
        ResponseClient response1 = UtilHttp.send("POST",
                "http://localhost:8080/users",
                "{\"login\": \"dolore\",\"name\": \"NickName\",\"email\": \"mail@mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}");
        assertEquals(200, response1.getCod());

        ResponseClient response2 = UtilHttp.send("POST",
                "http://localhost:8080/users",
                "{\"login\": \"dolore\",\"name\": \"NickName\",\"email\": \"mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}");
        assertEquals(400, response2.getCod(), "Тест на отсутствии знака @ в  емейле");

        ResponseClient response3 = UtilHttp.send("POST",
                "http://localhost:8080/users",
                "{\"login\": \"dolore\",\"name\": \"NickName\",\"birthday\": \"1986-08-20\"}");
        assertEquals(400, response3.getCod(), "Тест на отсутствие емейла");

        ResponseClient response4 = UtilHttp.send("POST",
                "http://localhost:8080/users",
                "{\"login\": \"dol ore\",\"name\": \"NickName\",\"email\": \"mail@mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}");
        assertEquals(500, response4.getCod(), "Тест на пробелы в логине");

        ResponseClient response5 = UtilHttp.send("POST",
                "http://localhost:8080/users",
                "{\"name\": \"NickName\",\"email\": \"mail@mail.ru\",\"birthday\": \"1986-08-20\"}");
        assertEquals(400, response5.getCod(), "Тест на пустой логин");

        ResponseClient response6 = UtilHttp.send("POST",
                "http://localhost:8080/users",
                "{\"login\": \"dolore\",\"name\": \"NickName\",\"email\": \"mail@mail.ru\"," +
                        "\"birthday\": \"2086-08-20\"}");
        assertEquals(400, response6.getCod(), "Тест на дату рождения");

        ResponseClient response7 = UtilHttp.send("POST", "http://localhost:8080/users",
                "{}");
        assertEquals(400, response7.getCod(), "Тест на пустое тело запроса");
    }

    @Test
    void userControllerPUTTest() {
        ResponseClient response1 = UtilHttp.send("POST", "http://localhost:8080/users",
                "{\"login\": \"dolore\",\"name\": \"NickName\",\"email\": \"mail@mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}");
        assertEquals(200, response1.getCod(), "Пользователь не добавился.");

        ResponseClient response8 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{\"id\": \"1\", \"login\": \"NEWlogin\",\"name\": \"New Name\",\"email\": \"NEWmail@mail.ru\"," +
                        "\"birthday\": \"1986-01-01\"}");
        assertEquals(200, response8.getCod(), "Данные пользователя не обновились.");

        ResponseClient response9 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{\"login\": \"NEWlogin\",\"name\": \"New Name\",\"email\": \"NEWmail@mail.ru\"," +
                        "\"birthday\": \"1986-01-01\"}");
        assertEquals(500, response9.getCod(), "Тест на отсутствие ID при обновлении.");

        ResponseClient response2 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{\"id\": \"1\", \"login\": \"dolore\",\"name\": \"NickName\",\"email\": \"mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}");
        assertEquals(400, response2.getCod(), "Тест на отсутствии знака @ в  емейле");

        ResponseClient response3 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{\"id\": \"1\", \"login\": \"dolore\",\"name\": \"NickName\",\"birthday\": \"1986-08-20\"}");
        assertEquals(400, response3.getCod(), "Тест на отсутствие емейла");

        ResponseClient response4 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{\"id\": \"1\", \"login\": \"dol ore\",\"name\": \"NickName\",\"email\": \"mail@mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}");
        assertEquals(500, response4.getCod(), "Тест на пробелы в логине");

        ResponseClient response5 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{\"id\": \"1\", \"name\": \"NickName\",\"email\": \"mail@mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}");
        assertEquals(400, response5.getCod(), "Тест на пустой логин");

        ResponseClient response6 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{\"id\": \"1\", \"login\": \"dolore\",\"name\": \"NickName\",\"email\": \"mail@mail.ru\"," +
                        "\"birthday\": \"2086-08-20\"}");
        assertEquals(400, response6.getCod(), "Тест на дату рождения");

        ResponseClient response7 = UtilHttp.send("PUT", "http://localhost:8080/users",
                "{}");
        assertEquals(400, response7.getCod(), "Тест на пустое тело запроса");
    }


    @Test
    void filmControllerPOSTTest() {
        ResponseClient response1 = UtilHttp.send("POST", "http://localhost:8080/films",
                "{\"name\": \"name Film\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(200, response1.getCod(), "Фильм не добавлен.");

        ResponseClient response2 = UtilHttp.send("POST", "http://localhost:8080/films", "{}");
        assertEquals(400, response2.getCod(), "Тест на пустое тело запроса");


        ResponseClient response3 = UtilHttp.send("POST", "http://localhost:8080/films",
                "{\"name\": \"\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(400, response3.getCod(), "Тест на пустое название фильма.");

        ResponseClient response4 = UtilHttp.send("POST", "http://localhost:8080/films",
                "{\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(400, response4.getCod(), "2й Тест на пустое название фильма.");

        ResponseClient response5 = UtilHttp.send("POST", "http://localhost:8080/films",
                "{\"name\": \"name Film\"," +
                        "\"description\": \"Пятеро друзей ( комик-группа «Шарло»), " +
                        "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, " +
                        "который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                        "который за время «своего отсутствия», стал кандидатом Коломбани.\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(400, response5.getCod(), "Тест, где описание фильма больше 200 символов.");

        ResponseClient response6 = UtilHttp.send("POST", "http://localhost:8080/films",
                "{\"name\": \"name Film\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1786-08-20\",\"duration\": \"200\"}");
        assertEquals(500, response6.getCod(), "Тест на дату релиза.");

        ResponseClient response7 = UtilHttp.send("POST", "http://localhost:8080/films",
                "{\"name\": \"name Film\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"-500\"}");
        assertEquals(400, response7.getCod(), "Тест на продолжительность фильма.");
    }

    @Test
    void filmControllerPUTTest() {
        ResponseClient response1 = UtilHttp.send("POST", "http://localhost:8080/films",
                "{\"name\": \"name Film\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(200, response1.getCod(), "Фильм не добавлен.");

        ResponseClient response2 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{}");
        assertEquals(400, response2.getCod(), "Тест на пустое тело запроса");


        ResponseClient response3 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{\"id\": \"1\",\"name\": \"\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(400, response3.getCod(), "Тест на пустое название фильма.");

        ResponseClient response4 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{\"id\": \"1\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(400, response4.getCod(), "2й Тест на пустое название фильма.");

        ResponseClient response5 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{\"id\": \"1\",\"name\": \"name Film\"," +
                        "\"description\": \"Пятеро друзей ( комик-группа «Шарло»), " +
                        "приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, " +
                        "который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                        "который за время «своего отсутствия», стал кандидатом Коломбани.\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"200\"}");
        assertEquals(400, response5.getCod(), "Тест, где описание фильма больше 200 символов.");

        ResponseClient response6 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{\"id\": \"1\",\"name\": \"name Film\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1786-08-20\",\"duration\": \"200\"}");
        assertEquals(500, response6.getCod(), "Тест на дату релиза.");

        ResponseClient response7 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{\"id\": \"1\",\"name\": \"name Film\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"-500\"}");
        assertEquals(400, response7.getCod(), "Тест на продолжительность фильма.");

        ResponseClient response8 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{\"name\": \"name Film\",\"description\": \"description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"500\"}");
        assertEquals(500, response8.getCod(), "Тест на отсутствие ID при обновлении.");

        ResponseClient response9 = UtilHttp.send("PUT", "http://localhost:8080/films",
                "{\"id\": \"1\",\"name\": \"new name Film\",\"description\": \" new description Film\", " +
                        "\"releaseDate\": \"1986-08-20\",\"duration\": \"500\"}");
        assertEquals(200, response9.getCod(), "Фильм не обновился.");
    }
}
