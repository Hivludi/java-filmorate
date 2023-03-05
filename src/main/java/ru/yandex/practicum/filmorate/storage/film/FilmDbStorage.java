package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyLikedException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("FilmDB")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDao genreDao, MpaDao mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    @Override
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        int filmId = simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue();
        if (film.getMpa() != null) {
            String sql = "update FILMS set MPA_ID = ? where FILM_ID = ?";
            jdbcTemplate.update(sql, film.getMpa().getId(), filmId);
        }
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            String insertLikesQuery = "insert into FILM_LIKES (FILM_ID, USER_ID) VALUES (?,?)";

            for (Integer user_id : film.getLikes()) {
                jdbcTemplate.update(insertLikesQuery, filmId, user_id);
            }
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenresQuery = "insert into FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?,?)";

            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(insertGenresQuery, filmId, g.getId());
            }
        }
        return findFilmById(filmId);
    }

    @Override
    public Optional<Film> update(Film film) {
        findFilmById(film.getId());
        String sql = "update FILMS set " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ? " +
                "where FILM_ID = ?";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        String mpaUpdateSql = "update FILMS set MPA_ID = ? where FILM_ID = ?";
        if (film.getMpa() != null) {
            jdbcTemplate.update(mpaUpdateSql, film.getMpa().getId(), film.getId());
        } else {
            jdbcTemplate.update(mpaUpdateSql, null, film.getId());
        }

        jdbcTemplate.update("delete from FILM_GENRES where FILM_ID = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenresQuery = "insert into FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?,?)";

            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(insertGenresQuery, film.getId(), g.getId());
            }
        }

        jdbcTemplate.update("delete from FILM_LIKES where FILM_ID = ?", film.getId());
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            String insertLikesQuery = "insert into FILM_LIKES (FILM_ID, USER_ID) VALUES (?,?)";

            for (Integer user_id : film.getLikes()) {
                jdbcTemplate.update(insertLikesQuery, film.getId(), user_id);
            }
        }

        return findFilmById(film.getId());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sql = "select * from FILMS where FILM_ID = ?";
        Optional<Film> film = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id).stream()
                .findAny();
        if (film.isEmpty())
            throw new ObjectNotFoundException(String.format("Фильм с идентификатором %s не найден", id));
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "select * from FILMS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> addLike(int userId, int filmId) {
        findFilmById(filmId);
        if (getLikes(filmId).contains(userId))
            throw new FilmAlreadyLikedException("Фильм уже содержит лайк от данного пользователя");
        String insertLikeQuery = "insert into FILM_LIKES (FILM_ID, USER_ID) VALUES (?,?)";
        jdbcTemplate.update(insertLikeQuery, filmId, userId);
        return findFilmById(filmId);
    }

    @Override
    public Optional<Film> removeLike(int userId, int filmId) {
        findFilmById(filmId);
        if (!getLikes(filmId).contains(userId))
            throw new LikeNotFoundException("Фильм не содержит лайк от данного пользователя");
        String deleteLikeQuery = "delete from FILM_LIKES where USER_ID = ? and FILM_ID = ?";
        jdbcTemplate.update(deleteLikeQuery, userId, filmId);
        return findFilmById(filmId);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        String showMostPopularFilmsQuery;
        if (genreId.isPresent() && year.isPresent()) {
            showMostPopularFilmsQuery = "select f.FILM_ID from FILMS f " +
                    "left join FILM_GENRES fg on f.FILM_ID = fg.FILM_ID " +
                    "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                    "where fg.GENRE_ID = ? " +
                    "and extract(year from f.RELEASE_DATE) = ? " +
                    "group by f.FILM_ID " +
                    "order by COUNT(fl.USER_ID) desc " +
                    "limit ?";
            return jdbcTemplate.queryForList(showMostPopularFilmsQuery, Integer.class, genreId.get(), year.get(), count)
                    .stream()
                    .map(this::findFilmById)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } else if (genreId.isPresent()) {
            showMostPopularFilmsQuery = "select f.FILM_ID from FILMS f " +
                    "left join FILM_GENRES fg on f.FILM_ID = fg.FILM_ID " +
                    "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                    "where fg.GENRE_ID = ? " +
                    "group by f.FILM_ID " +
                    "order by COUNT(fl.USER_ID) desc " +
                    "limit ?";
            return jdbcTemplate.queryForList(showMostPopularFilmsQuery, Integer.class, genreId.get(), count)
                    .stream()
                    .map(this::findFilmById)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } else if (year.isPresent()) {
            showMostPopularFilmsQuery = "select f.FILM_ID from FILMS f " +
                    "left join FILM_GENRES fg on f.FILM_ID = fg.FILM_ID " +
                    "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                    "where extract(year from f.RELEASE_DATE) = ? " +
                    "group by f.FILM_ID " +
                    "order by COUNT(fl.USER_ID) desc " +
                    "limit ?";
            return jdbcTemplate.queryForList(showMostPopularFilmsQuery, Integer.class, year.get(), count)
                    .stream()
                    .map(this::findFilmById)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } else {
            showMostPopularFilmsQuery = "select f.FILM_ID from FILMS f " +
                    "left join FILM_LIKES fl on f.FILM_ID = fl.FILM_ID " +
                    "group by f.FILM_ID " +
                    "order by COUNT(fl.USER_ID) desc " +
                    "limit ?";
            return jdbcTemplate.queryForList(showMostPopularFilmsQuery, Integer.class, count)
                    .stream()
                    .map(this::findFilmById)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteFilmById(Integer filmId) {
        findFilmById(filmId);
        String deleteFilmByIdQuery = "delete from FILMS where FILM_ID=?";
        jdbcTemplate.update(deleteFilmByIdQuery, filmId);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(Objects.requireNonNull(rs.getDate("RELEASE_DATE")).toLocalDate())
                .duration(rs.getInt("DURATION"))
                .likes(getLikes(rs.getInt("FILM_ID")))
                .mpa(mpaDao.getMpaById(rs.getInt("MPA_ID")).get())
                .genres(genreDao.listGenreByFilmId(rs.getLong("FILM_ID")))
                .build();
    }

    private Set<Integer> getLikes(int filmId) {
        String sql = "select USER_ID from FILM_LIKES where FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        return values;
    }
}
