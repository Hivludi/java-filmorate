package ru.yandex.practicum.filmorate.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.director.Director;
import ru.yandex.practicum.filmorate.genre.Genre;
import ru.yandex.practicum.filmorate.director.DirectorDao;
import ru.yandex.practicum.filmorate.genre.GenreDao;
import ru.yandex.practicum.filmorate.mpa.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("FilmDB")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final DirectorDao directorDao;

    @Override
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        int filmId = simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue();
        if (film.getMpa() != null) {
            String sql = "UPDATE films SET mpa_id = ? WHERE film_id = ?";
            jdbcTemplate.update(sql, film.getMpa().getId(), filmId);
        }
        addFilmParameters(film, filmId);

        return findFilmById(filmId);
    }

    @Override
    public Optional<Film> update(Film film) {
        findFilmById(film.getId());
        String sql = "UPDATE films SET " +
                "name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());

        String mpaupdateSql = "UPDATE films set mpa_id = ? WHERE film_id = ?";
        if (film.getMpa() != null) {
            jdbcTemplate.update(mpaupdateSql, film.getMpa().getId(), film.getId());
        } else {
            jdbcTemplate.update(mpaupdateSql, null, film.getId());
        }

        int filmId = film.getId();

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ?", filmId);
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", filmId);

        addFilmParameters(film, filmId);

        return findFilmById(film.getId());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        Optional<Film> film = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id).stream()
                .findAny();
        if (film.isEmpty())
            throw new ObjectNotFoundException(String.format("Фильм с идентификатором %s не найден", id));
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> addLike(int userId, int filmId) {
        findFilmById(filmId);
        if (getLikes(filmId).contains(userId))
            return findFilmById(filmId);
        String insertLikeQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?,?)";
        jdbcTemplate.update(insertLikeQuery, filmId, userId);
        return findFilmById(filmId);
    }

    @Override
    public Optional<Film> removeLike(int userId, int filmId) {
        findFilmById(filmId);
        if (!getLikes(filmId).contains(userId))
            throw new LikeNotFoundException("Фильм не содержит лайк от данного пользователя");
        String deleteLikeQuery = "DELETE FROM film_likes WHERE user_id = ? and film_id = ?";
        jdbcTemplate.update(deleteLikeQuery, userId, filmId);
        return findFilmById(filmId);
    }

    @Override
    public List<Film> showMostPopularFilms(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        String showMostPopularFilmsQuery = "SELECT f.film_id FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "WHERE (? IS NULL OR fg.genre_id = ?) " +
                "AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.queryForList(showMostPopularFilmsQuery, Integer.class,
                        genreId.orElse(null),
                        genreId.orElse(null),
                        year.orElse(null),
                        year.orElse(null),
                        count)
                .stream()
                .map(this::findFilmById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> showCommonFilms(int userId, int friendId) {
        String showCommonFilmsQuery = "SELECT f.film_id FROM films f " +
                "JOIN film_likes fl ON f.film_id = fl.film_id " +
                "WHERE f.film_id IN " +
                "(SELECT film_id FROM film_likes fl2 " +
                "WHERE user_id IN (?,?) " +
                "GROUP BY film_id " +
                "HAVING COUNT(user_id) = 2) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC";
        return jdbcTemplate.queryForList(showCommonFilmsQuery, Integer.class, userId, friendId)
                .stream()
                .map(this::findFilmById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilmById(Integer filmId) {
        findFilmById(filmId);
        String deleteFilmByIdQuery = "DELETE FROM films WHERE film_id=?";
        jdbcTemplate.update(deleteFilmByIdQuery, filmId);
    }

    public Collection<Film> showFilmsUserLikes(Integer userId) {
        String filmsUserLikesSQL =
                "SELECT f.* FROM films f LEFT JOIN film_likes fl ON (f.film_id = fl.film_id) WHERE fl.user_id = ?";

        return jdbcTemplate.query(filmsUserLikesSQL,
                (rs, rowNum) -> makeFilm(rs),
                userId);
    }

    @Override
    public Collection<Film> showRecommendations(Integer userId) {
        String filmPreferencesOtherUsersSQL =
                "SELECT f.* FROM films f LEFT JOIN film_likes fl ON (f.film_id = fl.film_id) WHERE fl.user_id = ?"
                        + " EXCEPT "
                        + " SELECT f2.* FROM films f2 "
                        + "LEFT JOIN film_likes fl ON (f2.film_id = fl.film_id) WHERE fl.user_id = ?";

        // Все пользователи, которым также нравится фильмы пользователя, которому нужна рекомендация
        Set<Integer> otherUserIds = showFilmsUserLikes(userId).stream()
                .collect(HashSet::new,
                        (collection, film) ->
                                collection.addAll(film.getLikes()),
                        Collection::addAll);

        otherUserIds.remove(userId);

        // Все понравившиеся фильмы, всех пользователей за исключением фильмов пользователя, которому нужна рекомендация
        return otherUserIds.stream()
                .collect(HashSet::new,
                        (collection, otherUserId) ->
                                collection.addAll(jdbcTemplate.query(filmPreferencesOtherUsersSQL,
                                        (rs, rowNum) -> makeFilm(rs),
                                        otherUserId,
                                        userId)),
                        Collection::addAll);
    }

    @Override
    public List<Film> findDirectorFilms(int directorId, String sortBy) {
        directorDao.getDirectorById(directorId);
        if (sortBy.equals("year")) {
            String sql = "SELECT * FROM films AS f WHERE f.film_id IN (" +
                        "SELECT fd.film_id FROM film_directors AS fd WHERE fd.director_id = ?" +
                    ") ORDER BY f.release_date";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), directorId);
        } else {
            String sql = "SELECT * FROM films AS f WHERE f.film_id IN (" +
                        "SELECT fd1.film_id FROM film_directors AS fd1 WHERE fd1.director_id = ?" +
                    ") ORDER BY (" +
                        "SELECT count(fl.user_id) FROM film_likes AS fl WHERE fl.film_id IN (" +
                            "SELECT fd2.film_id FROM film_directors AS fd2 WHERE fd2.director_id = ?" +
                        ") GROUP BY fl.film_id" +
                    ") DESC";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), directorId, directorId);
        }
    }

    @Override
    public List<Film> searchFilmsByNameOrDirector(String query) {
        String sqlQuery = "SELECT DISTINCT f.*, " +
                "m.name, " +
                "COUNT(fl.film_id)" +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "LEFT JOIN film_directors fd ON f.film_id=fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id=d.director_id " +
                "WHERE LOWER(f.name) LIKE ? OR LOWER(d.name) LIKE ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.film_id) DESC";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (makeFilm(rs)), query, query);
    }

    @Override
    public List<Film> searchFilmsByName(String query) {
        String sqlQuery = "SELECT DISTINCT f.*, " +
                "m.name, " +
                "COUNT(fl.film_id)  " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "WHERE LOWER(f.name) LIKE ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.film_id) DESC ";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (makeFilm(rs)), query);
    }

    @Override
    public List<Film> searchFilmsByDirector(String query) {
        String sqlQuery = "SELECT distinct f.*, " +
                "m.name, " +
                "COUNT(fl.film_id)  " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors d ON d.director_id = fd.director_id " +
                "WHERE LOWER(d.name) LIKE ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.film_id) DESC ";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> (makeFilm(rs)), query);
    }

    private void addFilmParameters(Film film, int filmId) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            StringBuilder insertGenresQuery = new StringBuilder("insert into FILM_GENRES (FILM_ID, GENRE_ID) VALUES ");
            int i = 0;
            for (Genre g : film.getGenres()) {
                if (i != 0) insertGenresQuery.append(", ");
                else i = 1;
                insertGenresQuery.append("(").append(filmId).append(",").append(g.getId()).append(")");
            }
            jdbcTemplate.update(insertGenresQuery.toString());
        }

        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            StringBuilder insertLikesQuery = new StringBuilder("insert into FILM_LIKES (FILM_ID, USER_ID) VALUES ");
            int i = 0;
            for (Integer user_id : film.getLikes()) {
                if (i != 0) insertLikesQuery.append(", ");
                else i = 1;
                insertLikesQuery.append("(").append(filmId).append(",").append(user_id).append(")");
            }
            jdbcTemplate.update(insertLikesQuery.toString());
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            StringBuilder insertDirectorsQuery =
                    new StringBuilder("insert into FILM_DIRECTORS (FILM_ID, director_id) VALUES ");
            int i = 0;
            for (Director d : film.getDirectors()) {
                if (i != 0) insertDirectorsQuery.append(", ");
                else i = 1;
                insertDirectorsQuery.append("(").append(filmId).append(",").append(d.getId()).append(")");
            }
            jdbcTemplate.update(insertDirectorsQuery.toString());
        }
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
                .directors(directorDao.listDirectorByFilmId(rs.getInt("FILM_ID")))
                .genres(genreDao.listGenreByFilmId(rs.getLong("FILM_ID")))
                .build();
    }

    private Set<Integer> getLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
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
