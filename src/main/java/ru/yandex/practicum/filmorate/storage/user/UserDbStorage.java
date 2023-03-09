package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyFriendedException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("UserDB")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        int userId = simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue();

        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            String sql = "INSERT INTO friends_list (user_id, friend_id) VALUES (?,?)";

            for (Integer friend_id : user.getFriends()) {
                jdbcTemplate.update(sql, userId, friend_id);
            }
        }

        return findUserById(userId);
    }

    @Override
    public Optional<User> update(User user) {
        findUserById(user.getId());
        String sql = "UPDATE users SET " +
                "email = ?, " +
                "login = ?, " +
                "name = ?, " +
                "birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        String deleteFriendsQuery = "DELETE FROM friends_list WHERE user_id = ?";
        jdbcTemplate.update(deleteFriendsQuery, user.getId());
        String insertFriendsQuery = "INSERT INTO friends_list (user_id, friend_id) VALUES (?,?)";

        if (user.getFriends() != null && !user.getFriends().isEmpty())
            for (Integer friend_id : user.getFriends()) {
                jdbcTemplate.update(insertFriendsQuery, user.getId(), friend_id);
            }

        return findUserById(user.getId());
    }

    @Override
    public Optional<User> findUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Optional<User> user = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id).stream()
                .findAny();
        if (user.isEmpty()) throw new ObjectNotFoundException(
                String.format("Пользователь с идентификатором %s не найден", id));
        return user;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> addFriend(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        if (getFriends(userId).contains(friendId))
            throw new UserAlreadyFriendedException(
                    String.format("Пользователь с идентификатором %s уже добавлен в друзья", friendId));
        String insertFriendQuery = "INSERT INTO friends_list (user_id, friend_id) VALUES (?,?)";
        jdbcTemplate.update(insertFriendQuery, userId, friendId);
        return findUserById(userId);
    }

    @Override
    public Optional<User> removeFriend(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        if (!getFriends(userId).contains(friendId))
            throw new FriendNotFoundException(
                    String.format("Пользователя с идентификатором %s нет в друзьях", friendId));
        String deleteFriendQuery = "DELETE FROM friends_list WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteFriendQuery, userId, friendId);
        return findUserById(userId);
    }

    @Override
    public List<User> showMutualFriends(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        String sql = "SELECT friend_id " +
                "FROM friends_list " +
                "WHERE user_id IN (?, ?) " +
                "GROUP BY friend_id " +
                "HAVING COUNT(user_id) = 2";

        return jdbcTemplate.queryForList(sql, Integer.class, userId, friendId).stream()
                .map(this::findUserById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> showFriendList(int userId) {
        findUserById(userId);
        String sql = "SELECT friend_id FROM friends_list WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId).stream()
                .map(this::findUserById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Integer userId){
        findUserById(userId);
        String deleteUserByIdQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(deleteUserByIdQuery, userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("USER_ID"))
                .email(rs.getString("EMAIL"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("NAME"))
                .birthday(Objects.requireNonNull(rs.getDate("BIRTHDAY")).toLocalDate())
                .friends(getFriends(rs.getInt("USER_ID")))
                .build();
    }

    private Set<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friends_list WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, userId));
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", user.getEmail());
        values.put("LOGIN", user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) values.put("NAME", user.getLogin());
        else values.put("NAME", user.getName());
        values.put("BIRTHDAY", user.getBirthday());
        return values;
    }
}
