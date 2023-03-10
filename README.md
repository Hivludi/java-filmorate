# java-filmorate
<img width="1424" src="Filmorate ERD.jpg" alt="ERD Diagram">

## Примеры запросов:
### Получить пользователя по id
~~~
SELECT *
FROM user
WHERE user_id = ?;
~~~
### Получить всех пользователей
~~~
SELECT *
FROM user;
~~~
### Получить все фильмы
~~~
SELECT *
FROM film;
~~~
### Получить топ N наиболее популярных фильмов
~~~
SELECT *
FROM film
WHERE film_id IN (
       SELECT film_id
       FROM film_likes
       GROUP BY film_id
       ORDER BY COUNT(user_id) DESC
       LIMIT N);
~~~
### Получить список общих друзей с другим пользователем
~~~
SELECT COUNT(user_id),
       friend_id
FROM friends_list
WHERE user_id IN (x1, x2)
GROUP BY friend_id
HAVING COUNT(user_id) = 2;
~~~
