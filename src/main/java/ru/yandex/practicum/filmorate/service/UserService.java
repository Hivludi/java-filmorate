package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public UserService (@Qualifier("UserDB") UserStorage userStorage, @Qualifier("FilmDB") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Optional<User> addFriend(int userId, int friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public Optional<User> removeFriend(int userId, int friendId) {
        return userStorage.removeFriend(userId, friendId);
    }

    public List<User> showMutualFriends(int userId, int friendId) {
        return userStorage.showMutualFriends(userId, friendId);
    }

    public List<User> showFriendList(int userId) {
        return userStorage.showFriendList(userId);
    }

    public Optional<User> create(User user) {
        return userStorage.create(user);
    }

    public Optional<User> update(User user) {
        return userStorage.update(user);
    }

    public Optional<User> findUserById(int id) {
        return userStorage.findUserById(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void deleteUserById(Integer userId){
        userStorage.deleteUserById(userId);
    }

    public Collection<Film> showRecommendations(Integer userId) {
        return filmStorage.showRecommendations(userId);
    }

}
