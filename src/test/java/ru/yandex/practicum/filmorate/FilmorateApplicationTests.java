package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.film.Film;
import ru.yandex.practicum.filmorate.mpa.Mpa;
import ru.yandex.practicum.filmorate.user.User;
import ru.yandex.practicum.filmorate.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;

	@BeforeEach
	public void setUp() {
		User user1 = User.builder()
				.email("mail@mail.ru")
				.login("dolore")
				.name("Nick Name")
				.birthday(LocalDate.parse("1946-08-20"))
				.build();

		User user2 = User.builder()
				.email("mailsd@mail.ru")
				.login("dolore2")
				.name("Nick Name2")
				.birthday(LocalDate.parse("1966-09-11"))
				.build();

		userStorage.create(user1);
		userStorage.create(user2);

		Film film1 = Film.builder()
				.name("film1")
				.description("desc1")
				.releaseDate(LocalDate.parse("1996-09-11"))
				.duration(120)
				.mpa(new Mpa(1, "G"))
				.build();

		Film film2 = Film.builder()
				.name("film2")
				.description("desc2")
				.releaseDate(LocalDate.parse("1998-09-11"))
				.duration(130)
				.mpa(new Mpa(1, "G"))
				.build();

		filmStorage.create(film1);
		filmStorage.create(film2);
	}

	@Test
	public void testFindUserById() {

		Optional<User> userOptional1 = userStorage.findUserById(1);
		Optional<User> userOptional2 = userStorage.findUserById(2);

		assertThat(userOptional1)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);

		assertThat(userOptional2)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 2)
				);
	}

	@Test
	public void testFindAllUsers() {

		Collection<User> userCollection = userStorage.findAll();

		assertThat(userCollection.size() == 2);
	}

	@Test
	public void testUpdateUser() {
		User user1 = User.builder()
				.id(1)
				.email("mail2@mail.ru")
				.login("dolore2")
				.name("Nick Name2")
				.birthday(LocalDate.parse("1946-08-20"))
				.build();

		Optional<User> userOptional = userStorage.update(user1);
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("login", "dolore2")
				);
	}

	@Test
	public void testFindFilmById() {

		Optional<Film> filmOptional1 = filmStorage.findFilmById(1);
		Optional<Film> filmOptional2 = filmStorage.findFilmById(2);

		assertThat(filmOptional1)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1)
				);

		assertThat(filmOptional2)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 2)
				);
	}

	@Test
	public void testFindAllFilms() {

		Collection<Film> filmCollection = filmStorage.findAll();

		assertThat(filmCollection.size() == 2);
	}

	@Test
	public void testUpdateFilm() {
		Film film1 = Film.builder()
				.id(1)
				.name("film4")
				.description("desc4")
				.releaseDate(LocalDate.parse("1991-09-11"))
				.duration(160)
				.mpa(new Mpa(2, "PG"))
				.build();

		Optional<Film> filmOptional = filmStorage.update(film1);
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("name", "film4")
				);
	}
}
