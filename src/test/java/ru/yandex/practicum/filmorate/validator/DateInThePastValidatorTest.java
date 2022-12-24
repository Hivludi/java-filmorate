package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateInThePastValidatorTest {

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
    public void userBirthdayShouldNotBeInTheFuture() {
        assertTrue(user.getBirthday().isBefore(LocalDate.now()));
        user = user.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        assertFalse(user.getBirthday().isBefore(LocalDate.now()));
    }
}
