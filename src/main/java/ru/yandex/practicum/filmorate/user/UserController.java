package ru.yandex.practicum.filmorate.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.film.Film;
import ru.yandex.practicum.filmorate.feed.FeedEvent;
import ru.yandex.practicum.filmorate.feed.FeedService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final FeedService feedService;

    @PostMapping
    public Optional<User> create(@Validated @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public Optional<User> update(@Validated @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable(value = "id") Integer userId) {
        return userService.findUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Optional<User> addFriend(@PathVariable(value = "id") Integer userId,
                                    @PathVariable Integer friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Optional<User> removeFriend(@PathVariable(value = "id") Integer userId,
                                       @PathVariable Integer friendId) {
        return userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> showFriendsList(@PathVariable(value = "id") Integer userId) {
        return userService.showFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> showMutualFriends(@PathVariable(value = "id") Integer userId,
                                        @PathVariable(value = "otherId") Integer user2Id) {
        return userService.showMutualFriends(userId, user2Id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable(value = "userId") Integer userId){
        userService.deleteUserById(userId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> showRecommendations(@PathVariable(value = "id") Integer userId) {
        return userService.showRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEvent> getUserFeed(@PathVariable(value = "id") Integer id) {
        return feedService.getUserFeed(id);
    }
}