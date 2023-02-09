# java-filmorate
<img width="800" src="https://github.com/Hivludi/java-filmorate/blob/00a5aa04dbfd5e2e6be732e31bfb56d272e0745a/Filmorate%20ERD%20v2.jpg">

## Примеры запросов:
### Получить конкретного пользователя
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
