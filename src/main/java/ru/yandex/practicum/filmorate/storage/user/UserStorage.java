package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> create(User user);

    Optional<User> update(User user);

    Optional<User> findUserById(int id);

    Collection<User> findAll();

    Optional<User> addFriend(int userId, int friendId);

    Optional<User> removeFriend(int userId, int friendId);

    List<User> showMutualFriends(int userId, int friendId);

    List<User> showFriendList(int userId);

}