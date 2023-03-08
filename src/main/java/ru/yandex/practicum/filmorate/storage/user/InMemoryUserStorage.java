package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyFriendedException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("UserInMemory")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public Optional<User> create(User user) {
        user.setId(idGenerator);
        idGenerator++;
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User user) {
        if (!users.containsKey(user.getId())) throw new ObjectNotFoundException("Пользователь не существует");
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return Optional.of(user);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findUserById(int id) {
        if (users.get(id) == null) throw new ObjectNotFoundException("Пользователь не существует");
        return Optional.of(users.get(id));
    }

    @Override
    public Optional<User> addFriend(int userId, int friendId) {
        Set<Integer> user1Friends = findUserById(userId).get().getFriends();
        Set<Integer> user2Friends = findUserById(friendId).get().getFriends();
        if (user1Friends.contains(friendId) || user2Friends.contains(userId)) throw new UserAlreadyFriendedException("Пользователь уже добавлен в друзья");
        user1Friends.add(friendId);
        user2Friends.add(userId);
        return findUserById(userId);
    }

    @Override
    public Optional<User> removeFriend(int userId, int friendId) {
        Set<Integer> user1Friends = findUserById(userId).get().getFriends();
        Set<Integer> user2Friends = findUserById(friendId).get().getFriends();
        if (!user1Friends.contains(friendId) || !user2Friends.contains(userId)) throw new FriendNotFoundException("Пользователя нет в друзьях");
        user1Friends.remove(friendId);
        user2Friends.remove(userId);
        return findUserById(userId);
    }

    @Override
    public List<User> showMutualFriends(int userId, int friendId) {
        List<User> user1Friends = showFriendList(userId);
        List<User> user2Friends = showFriendList(friendId);
        if (user1Friends.isEmpty() || user2Friends.isEmpty()) return new ArrayList<>();
        return user1Friends.stream()
                .filter(user2Friends::contains)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> showFriendList(int userId) {
        Set<Integer> friendIds = findUserById(userId).get().getFriends();
        if (friendIds == null) return new ArrayList<>();
        return friendIds.stream()
                .map(this::findUserById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteUserById(Integer userId){
        if (users.containsKey(userId)) {
            users.remove(userId, users.get(userId));
            log.info("Пользователь с id= " + userId + " удалён");
        } else {
            throw new ObjectNotFoundException("Пользователя с id= " + userId + " не существует");
        }
    }
}
