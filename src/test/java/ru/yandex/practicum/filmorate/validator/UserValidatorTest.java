package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = User
                .builder()
                .id(1)
                .email("test@mail.com")
                .login("testLogin")
                .name("testName")
                .birthday(LocalDate.of(1990, 11, 21))
                .build();
    }

    @Test
    public void emailShouldNotBeBlankOrEmptyAndShouldContainAtSign() {
        assertTrue(UserValidator.isValid(user));
        user = user.toBuilder().email("").build();
        assertFalse(UserValidator.isValid(user));
        user = user.toBuilder().email("      ").build();
        assertFalse(UserValidator.isValid(user));
        user = user.toBuilder().email("emaildotcom").build();
        assertFalse(UserValidator.isValid(user));
    }

    @Test
    public void loginShouldNotBeBlankOrEmptyOrContainSpaces() {
        assertTrue(UserValidator.isValid(user));
        user = user.toBuilder().login("").build();
        assertFalse(UserValidator.isValid(user));
        user = user.toBuilder().login("      ").build();
        assertFalse(UserValidator.isValid(user));
        user = user.toBuilder().login("email dotcom").build();
        assertFalse(UserValidator.isValid(user));
    }

    @Test
    public void nameShouldBeReplacedWithLoginIfBlankOrEmpty() {
        assertNotEquals(user.getName(), user.getLogin());
        user = user.toBuilder().name("      ").build();
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        assertEquals(user.getName(), user.getLogin());
        user = user.toBuilder().name("").build();
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void birthDayShouldNotBeInTheFuture() {
        assertTrue(UserValidator.isValid(user));
        user = user.toBuilder().birthday(LocalDate.now()).build();
        assertTrue(UserValidator.isValid(user));
        user = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        assertFalse(UserValidator.isValid(user));
    }

}
