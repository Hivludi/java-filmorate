package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RecomendationsTest {
    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testRecomendations() {
        assertThat(filmStorage.showRecommendations(100), equalTo(Set.of()));

        assertThat(filmStorage.showRecommendations(2), equalTo(Set.of(TestEnv.film3, TestEnv.film2)));

        jdbcTemplate.update("INSERT INTO FILM_LIKES (film_id, user_id) VALUES(3, 2)");
        assertThat(filmStorage.showRecommendations(2), equalTo(Set.of(TestEnv.film2, TestEnv.film4)));

        jdbcTemplate.update("INSERT INTO FILM_LIKES (film_id, user_id) VALUES(4, 2)");
        assertThat(filmStorage.showRecommendations(2), equalTo(Set.of(TestEnv.film2)));

        jdbcTemplate.update("INSERT INTO FILM_LIKES (film_id, user_id) VALUES(2, 2)");
        assertThat(filmStorage.showRecommendations(2), equalTo(Set.of()));
    }

    @BeforeEach
    void beforeEach() {
        String filmsSQL =
                "   INSERT INTO FILMS (name, description, release_date, duration, MPA_ID)"
                + " VALUES  ('Back to the Future', 'Seventeen-year-old Marty McFly came home early yesterday', '1985-07-03', 116, 2),"
                + "         ('Pulp Fiction', 'Just because you are a character does not mean you have character', '1994-05-21', 154, 4),"
                + "         ('WALL·E', 'Love is a matter of technique', '2008-06-21', 98, 1),"
                + "         ('Inception', 'Your mind is a crime scene', '2010-07-08', 148, 3),"
                + "         ('Home Alone', 'When Kevin Family Left For Vacation, They Forgot One Minor Detail: Kevin', '1990-11-10', 103, 2);";
        String filmsGenresSQL =
                "   INSERT INTO FILM_GENRES (film_id, genre_id)"
                + " VALUES (1, 1), (1, 6), (2, 2), (2, 6), (3, 3), (3, 1), (4, 6), (4, 4), (4, 2), (5, 1);";
        String usersSQL =
                "   INSERT INTO users (email, login, name, birthday)"
                + " VALUES  ('user1@mail.ru', 'user1', 'Jack1', '2000-10-10'),"
                + "         ('user2@mail.ru', 'user2', 'John2', '2001-11-15'),"
                + "         ('user3@mail.ru', 'user3', 'Joe3', '1999-01-10'),"
                + "         ('user4@mail.ru', 'user4', 'Hank4', '2000-05-11'),"
                + "         ('user5@mail.ru', 'user5', 'Mike5', '1995-12-10');";
        String usersFilmsSQL =
                "   INSERT INTO FILM_LIKES (film_id, user_id)"
                + " VALUES (1, 1), (1, 2), (1, 3), (2, 1), (2, 5), (3, 1), (3, 3), (3, 4), (3, 5), (4, 5);";

        jdbcTemplate.update(filmsSQL);
        jdbcTemplate.update(filmsGenresSQL);
        jdbcTemplate.update(usersSQL);
        jdbcTemplate.update(usersFilmsSQL);
        TestEnv.beforeEach();
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.update("DELETE FROM FILM_GENRES;");
        jdbcTemplate.update("DELETE FROM FRIENDS_LIST;");
        jdbcTemplate.update("DELETE FROM FILM_LIKES;");
        jdbcTemplate.update("DELETE FROM FILMS;");
        jdbcTemplate.update("DELETE FROM USERS;");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1;");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;");
    }

    static class TestEnv {
        static User user1, user2, user3, user4, user5;
        static Film film1, film2, film3, film4;
        static Mpa mpa1, mpa2, mpa3, mpa4, mpa5;
        static Genre genre1, genre2, genre3, genre4, genre5, genre6;

        static void beforeEach() {
            mpa1 = Mpa.builder()
                    .id(1)
                    .name("G")
                    .build();

            mpa2 = Mpa.builder()
                    .id(2)
                    .name("PG")
                    .build();

            mpa3 = Mpa.builder()
                    .id(3)
                    .name("PG-13")
                    .build();

            mpa4 = Mpa.builder()
                    .id(4)
                    .name("R")
                    .build();

            mpa5 = Mpa.builder()
                    .id(5)
                    .name("NC-17")
                    .build();

            genre1 = Genre.builder()
                    .id(1)
                    .name("Комедия")
                    .build();

            genre2 = Genre.builder()
                    .id(2)
                    .name("Драма")
                    .build();

            genre3 = Genre.builder()
                    .id(3)
                    .name("Мультфильм")
                    .build();

            genre4 = Genre.builder()
                    .id(4)
                    .name("Триллер")
                    .build();

            genre5 = Genre.builder()
                    .id(5)
                    .name("Документальный")
                    .build();

            genre6 = Genre.builder()
                    .id(6)
                    .name("Боевик")
                    .build();

            user1 = User.builder()
                    .id(1)
                    .name("Jack1")
                    .login("user1")
                    .email("user1@mail.ru")
                    .birthday(LocalDate.parse("2000-10-10"))
                    .friends(Set.of())
                    .build();

            user2 = User.builder()
                    .id(2)
                    .name("John2")
                    .login("user2")
                    .email("user2@mail.ru")
                    .birthday(LocalDate.parse("2001-11-15"))
                    .friends(Set.of())
                    .build();

            user3 = User.builder()
                    .id(3)
                    .name("Joe3")
                    .login("user3")
                    .email("user3@mail.ru")
                    .birthday(LocalDate.parse("1999-01-10"))
                    .friends(Set.of())
                    .build();

            user4 = User.builder()
                    .id(4)
                    .name("Hank4")
                    .login("user4")
                    .email("user4@mail.ru")
                    .birthday(LocalDate.parse("2000-05-11"))
                    .friends(Set.of())
                    .build();

            user5 = User.builder()
                    .id(5)
                    .name("Mike5")
                    .login("user5")
                    .email("user5@mail.ru")
                    .birthday(LocalDate.parse("1995-12-10"))
                    .friends(Set.of())
                    .build();

            film1 = Film.builder()
                    .id(1)
                    .name("Back to the Future")
                    .description("Seventeen-year-old Marty McFly came home early yesterday")
                    .releaseDate(LocalDate.parse("1985-07-03"))
                    .duration(116)
                    .mpa(mpa2)
                    .genres(Set.of(genre1, genre6))
                    .likes(Set.of(1, 2, 3))
                    .build();

            film2 = Film.builder()
                    .id(2)
                    .name("Pulp Fiction")
                    .description("Just because you are a character does not mean you have character")
                    .releaseDate(LocalDate.parse("1994-05-21"))
                    .duration(154)
                    .mpa(mpa4)
                    .genres(Set.of(genre6, genre2))
                    .likes(Set.of(5, 1))
                    .build();

            film3 = Film.builder()
                    .id(3)
                    .name("WALL·E")
                    .description("Love is a matter of technique")
                    .releaseDate(LocalDate.parse("2008-06-21"))
                    .duration(98)
                    .mpa(mpa1)
                    .genres(Set.of(genre3, genre1))
                    .likes(Set.of(1, 3, 4, 5))
                    .build();

            film4 = Film.builder()
                    .id(4)
                    .name("Inception")
                    .description("Your mind is a crime scene")
                    .releaseDate(LocalDate.parse("2010-07-08"))
                    .duration(148)
                    .mpa(mpa3)
                    .genres(Set.of(genre2, genre6, genre4))
                    .likes(Set.of(5))
                    .build();
        }

    }

}
