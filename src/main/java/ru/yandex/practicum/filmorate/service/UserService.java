package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyFriendedException;
import ru.yandex.practicum.filmorate.exceptions.FriendNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(long userid1, long userid2) {
        Set<Long> user1Friends = userStorage.findUserById(userid1).getFriends();
        Set<Long> user2Friends = userStorage.findUserById(userid2).getFriends();
        if (user1Friends.contains(userid2) || user2Friends.contains(userid1)) throw new UserAlreadyFriendedException("Пользователь уже добавлен в друзья");
        user1Friends.add(userid2);
        user2Friends.add(userid1);
    }

    public void removeFriend(long userid1, long userid2) {
        Set<Long> user1Friends = userStorage.findUserById(userid1).getFriends();
        Set<Long> user2Friends = userStorage.findUserById(userid2).getFriends();
        if (!user1Friends.contains(userid2) || !user2Friends.contains(userid1)) throw new FriendNotFoundException("Пользователя нет в друзьях");
        user1Friends.remove(userid2);
        user2Friends.remove(userid1);
    }

    public List<User> showMutualFriends(long userid1, long userid2) {
        List<User> user1Friends = showFriendList(userid1);
        List<User> user2Friends = showFriendList(userid2);
        if (user1Friends.isEmpty() || user2Friends.isEmpty()) return new ArrayList<>();
        return user1Friends.stream()
                .filter(user2Friends::contains)
                .collect(Collectors.toList());
    }

    public List<User> showFriendList(long userId) {
        Set<Long> friendIds = userStorage.findUserById(userId).getFriends();
        if (friendIds == null) return new ArrayList<>();
        return friendIds.stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }
}
