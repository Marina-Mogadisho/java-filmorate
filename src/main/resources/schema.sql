-- IF NOT EXISTS   - Если таблица существует,
--команда не выполняет никаких действий и не выдаёт ошибку. Если таблицы нет, она будет
CREATE TABLE IF NOT EXISTS users
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	login varchar(255) NOT NULL,
	birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa
(
     mpa_id INT NOT NULL PRIMARY KEY,
     mpa_name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	title varchar(255) NOT NULL,
	description varchar(255) NOT NULL,
	release_date date NOT NULL,
	duration int NOT NULL,
	mpa_id int REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS friendships
(
     user_id int REFERENCES users (id),
     friend_id int REFERENCES users (id),
     PRIMARY KEY(user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS like_film
(
     user_id int REFERENCES users (id),
     film_id int REFERENCES films (id),
     PRIMARY KEY(user_id, film_id)
);

CREATE TABLE IF NOT EXISTS genre
(
     genre_id INT NOT NULL PRIMARY KEY,
     genre_name varchar(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS film_genre
(
     film_id INT REFERENCES films (id),
     genre_id INT REFERENCES genre (genre_id),
     PRIMARY KEY(film_id, genre_id)
);
