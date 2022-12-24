package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@Validated @RequestBody User user) {
        return userService.getUserStorage().create(user);
    }

    @PutMapping
    public User update(@Validated @RequestBody User user) {
        return userService.getUserStorage().update(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.getUserStorage().findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(value = "id") Long userId) {
        return userService.getUserStorage().findUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id") Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(value = "id") Long userId, @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> showFriendsList(@PathVariable(value = "id") Long userId) {
        return userService.showFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> showMutualFriends(@PathVariable(value = "id") Long userId, @PathVariable(value = "otherId") Long user2Id) {
        return userService.showMutualFriends(userId, user2Id);
    }
}