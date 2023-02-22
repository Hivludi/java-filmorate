package ru.yandex.practicum.filmorate.storage.user;

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
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();

        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            String sql = "insert into FRIENDS_LIST (USER_ID, FRIEND_ID) VALUES (?,?)";

            for (Integer friend_id : user.getFriends()) {
                jdbcTemplate.update(sql, userId, friend_id);
            }
        }

        return findUserById(userId);
    }

    @Override
    public Optional<User> update(User user) {
        findUserById(user.getId());
        String sql = "update USERS set " +
                "EMAIL = ?, " +
                "LOGIN = ?, " +
                "NAME = ?, " +
                "BIRTHDAY = ? " +
                "where USER_ID = ?";
        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        String deleteFriendsQuery = "delete from FRIENDS_LIST where USER_ID = ?";
        jdbcTemplate.update(deleteFriendsQuery, user.getId());
        String insertFriendsQuery = "insert into FRIENDS_LIST (USER_ID, FRIEND_ID) VALUES (?,?)";

        if (user.getFriends() != null && !user.getFriends().isEmpty())
            for (Integer friend_id : user.getFriends()) {
                jdbcTemplate.update(insertFriendsQuery, user.getId(), friend_id);
            }

        return findUserById(user.getId());
    }

    @Override
    public Optional<User> findUserById(int id) {
        String sql = "select * from USERS where USER_ID = ?";
        Optional<User> user = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id).stream()
                .findAny();
        if (user.isEmpty()) throw new ObjectNotFoundException(String.format("Пользователь с идентификатором %s не найден", id));
        return user;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> addFriend(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        if (getFriends(userId).contains(friendId)) throw new UserAlreadyFriendedException(String.format("Пользователь с идентификатором %s уже добавлен в друзья", friendId));
        String insertFriendQuery = "insert into FRIENDS_LIST (USER_ID, FRIEND_ID) VALUES (?,?)";
        jdbcTemplate.update(insertFriendQuery, userId, friendId);
        return findUserById(userId);
    }

    @Override
    public Optional<User> removeFriend(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        if (!getFriends(userId).contains(friendId)) throw new FriendNotFoundException(String.format("Пользователя с идентификатором %s нет в друзьях", friendId));
        String deleteFriendQuery = "delete from FRIENDS_LIST where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(deleteFriendQuery, userId, friendId);
        return findUserById(userId);
    }

    @Override
    public List<User> showMutualFriends(int userId, int friendId) {
        findUserById(userId);
        findUserById(friendId);
        String sql = "SELECT FRIEND_ID " +
                "FROM FRIENDS_LIST " +
                "WHERE USER_ID IN (?, ?) " +
                "GROUP BY FRIEND_ID " +
                "HAVING COUNT(USER_ID) = 2";

        return jdbcTemplate.queryForList(sql, Integer.class, userId, friendId).stream()
                .map(this::findUserById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> showFriendList(int userId) {
        findUserById(userId);
        String sql = "select FRIEND_ID from FRIENDS_LIST where USER_ID = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId).stream()
                .map(this::findUserById)
                .map(Optional::get)
                .collect(Collectors.toList());
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
        String sql = "select FRIEND_ID from FRIENDS_LIST where USER_ID = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, userId));
    }
}
