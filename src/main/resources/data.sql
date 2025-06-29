delete from friendships;
delete from like_film;
delete from users;
alter table users alter column id restart with 1;
delete from FILM_GENRE;
delete from films;
alter table films alter column id restart with 1;
delete from mpa;
delete from genre;
insert into mpa (mpa_id, mpa_name)
     values (1, 'G'),
            (2, 'PG'),
            (3, 'PG-13'),
            (4, 'R'),
            (5, 'NC-17')
            ;
insert into genre (genre_id, genre_name)
                 values (1, 'Комедия'),
                        (2, 'Драма'),
                        (3, 'Мультфильм'),
                        (4, 'Триллер'),
                        (5, 'Документальный'),
                        (6, 'Боевик')
                        ;

INSERT INTO users (name, email, login, birthday)
VALUES ('Marina', 'mar@mail.com', 'mar', '2000-12-15');

INSERT INTO films (title, description, release_date, duration, mpa_id)
VALUES ('Titanic', 'Love', '1998-03-01', '120', 1);





