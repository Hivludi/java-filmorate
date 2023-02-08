# java-filmorate
<img width="800" src="https://github.com/Hivludi/java-filmorate/blob/11f90d64a750140dd8c92aaeae65b8b2fc1fa041/ERDfilmorate.png">

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
ORDER BY likes_count DESC
LIMIT N;
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
