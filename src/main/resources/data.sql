delete from FRIENDS_LIST;
delete from FILM_LIKES;
delete from FILM_GENRES;
delete from REVIEWS; -- порядок важен для соблюдения целостности таблиц
delete from REVIEW_LIKES; -- порядок важен для соблюдения целостности таблиц
delete from FILMS;
delete from USERS;
delete from GENRES;
delete from MPA;

INSERT INTO MPA (NAME) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO GENRES (NAME)
VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

/*
INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES ('user1@mail.ru', 'user1', 'user1 userov1', '1994-10-01'),
       ('user2@mail.ru', 'user2', 'user2 userov2', '1995-11-02'),
       ('user3@mail.ru', 'user3', 'user3 userov3', '1996-12-03');
INSERT INTO  FRIENDS_LIST (USER_ID, FRIEND_ID)
VALUES (1,2), (1,3), (2,1), (2,3), (3,1), (3,2);
INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES ('default comedy 1', 'comedy 1', '1984-01-01', 91, 1),
       ('default drama 1', 'drama 1', '1985-02-02', 102, 2),
       ('default cartoon 1', 'cartoon 1', '1986-03-03', 113, 3),
       ('default thriller 1', 'thriller 1', '1987-04-04', 124, 4),
       ('default documentary 1', 'documentary 1', '1988-05-05', 135, 5),
       ('default action movie 1', 'action movie 1', '1989-06-06', 146, 1),
       ('default multigenre 1', 'multigenre 1', '1990-07-07', 157, 2);
INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID)
VALUES (1,1), (2,2), (3,3), (4,4), (5,5), (6,6), (7,1), (7,2), (7,3);
INSERT INTO FILM_LIKES (FILM_ID, USER_ID)
VALUES (1,1), (1,2), (1,3), (2,1), (2,2), (3,1), (4,1), (4,2), (4,3), (5,1), (5,2), (6,1), (7,1), (7,2), (7,3);
*/
/*
INSERT INTO REVIEWS (CONTENT, USER_ID, FILM_ID, IS_POSITIVE)
VALUES ('Indian film, perfect', 1, 1, true),
       ('Hollywood film in best practice, sad', 2, 2, true),
       ('Author cinema, unclear', 3, 3, false),
       ('Korean film, cruel', 1, 2, true),
       ('Bollywood film in best practice, melodrama', 2, 3, true),
       ('Horror cinema, blody', 3, 1, false);
INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE)
VALUES (1, 2, true),
       (2, 3, true),
       (3, 3, false);
*/



